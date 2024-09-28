package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.WorkshopModInstallProgressWebsocketHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.exception.CouldNotReadModMetaFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.MetaCppFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.SystemUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotDownloadWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotInstallWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotUpdateArmaServerException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.RetryableException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamCmdPathNotSetException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamTaskHandleException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.QueuedSteamTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamCmdAppUpdateParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamCmdWorkshopDownloadParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopModInstallSteamTask;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.lang.String.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class SteamCmdHandler
{
    private static final Deque<QueuedSteamTask> STEAM_TASKS = new ConcurrentLinkedDeque<>();
    private final Map<SteamTask.Type, Consumer<SteamTask>> steamTaskHandlers = Map.of(
            SteamTask.Type.WORKSHOP_DOWNLOAD, this::installWorkshopMod,
            SteamTask.Type.GAME_UPDATE, this::gameUpdate
    );

    private final ASWGConfig aswgConfig;
    private final ModStorage modStorage;
    private final InstalledModRepository installedModRepository;
    private final SteamWebApiService steamWebApiService;
    private final SteamTaskRetryPolicy steamTaskRetryPolicy;
    private final WorkshopModInstallProgressWebsocketHandler workshopModInstallProgressWebsocketHandler;

    private Thread steamCmdThread;
    private Thread steamCmdErrorThread;

    private QueuedSteamTask currentlyProcessingTask;

    public UUID queueSteamTask(SteamTask steamTask)
    {
        UUID uuid = UUID.randomUUID();
        queueTask(new QueuedSteamTask(uuid, steamTask, 0));
        return uuid;
    }

    public boolean hasFinished(UUID taskId)
    {
        if (this.currentlyProcessingTask != null && this.currentlyProcessingTask.getId().equals(taskId))
            return false;
        if (STEAM_TASKS.stream().anyMatch(task -> task.getId().equals(taskId)))
            return false;
        return true;
    }

    public List<SteamTask> getSteamTasks(SteamTask.Type type)
    {
        List<SteamTask> steamTasks = new ArrayList<>(STEAM_TASKS.stream()
                .map(QueuedSteamTask::getSteamTask)
                .filter(queuedSteamTask -> queuedSteamTask.getType().equals(type))
                .toList());

        if (this.currentlyProcessingTask != null && this.currentlyProcessingTask.getSteamTask().getType().equals(type))
            steamTasks.add(this.currentlyProcessingTask.getSteamTask());

        return steamTasks;
    }

    public boolean isSteamCmdBusy()
    {
        return this.currentlyProcessingTask != null;
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    private void handleSteamTasks()
    {
        if (STEAM_TASKS.isEmpty())
            return;

        QueuedSteamTask queuedSteamTask = STEAM_TASKS.poll();
        try
        {
            this.currentlyProcessingTask = queuedSteamTask;
            handleSteamTask(queuedSteamTask.getSteamTask());
            this.currentlyProcessingTask = null;
        }
        catch (Exception exception)
        {
            log.warn("Exception during handling of steam task.", exception);
            if (exception instanceof RetryableException && steamTaskRetryPolicy.canRetry(queuedSteamTask))
            {
                log.info("Requeue steam task: {}", queuedSteamTask);
                queueTask(new QueuedSteamTask(queuedSteamTask.getId(), queuedSteamTask.getSteamTask(), queuedSteamTask.getRetryCount() + 1));
            }
        }
    }

    private void queueTask(QueuedSteamTask queuedSteamTask)
    {
        if (STEAM_TASKS.stream().anyMatch(task -> task.getSteamTask().equals(queuedSteamTask.getSteamTask())))
        {
            log.info("Steam task {} already queued. Skipping.", queuedSteamTask);
            return;
        }

        log.info("Queueing steam task: {}", queuedSteamTask);
        if (queuedSteamTask.getSteamTask().getType() == SteamTask.Type.GAME_UPDATE)
        {
            // Game update has always first priority
            STEAM_TASKS.offerFirst(queuedSteamTask);
        }
        else
        {
            STEAM_TASKS.add(queuedSteamTask);
            publishMessage(new WorkshopModInstallationStatus(((WorkshopModInstallSteamTask)queuedSteamTask.getSteamTask()).getFileId(), 0));
        }
    }

    private void handleSteamTask(SteamTask steamTask)
    {
        steamTaskHandlers.get(steamTask.getType()).accept(steamTask);
    }

    private void gameUpdate(SteamTask steamTask)
    {
        String steamCmdPath = this.aswgConfig.getSteamCmdPath();
        if (!StringUtils.hasText(steamCmdPath))
            throw new SteamCmdPathNotSetException();
        try
        {
            performArmaUpdate(SteamCmdAppUpdateParameters.builder()
                    .appId(SteamUtils.ARMA_SERVER_APP_ID)
                    .serverDirectoryPath(this.aswgConfig.getServerDirectoryPath())
                    .steamCmdPath(steamCmdPath)
                    .steamUsername(this.aswgConfig.getSteamCmdUsername())
                    .steamPassword(this.aswgConfig.getSteamCmdPassword())
                    .build()).join();
        }
        catch (CompletionException e)
        {
            throw new SteamTaskHandleException(new CouldNotUpdateArmaServerException(e.getMessage()));
        }
    }

    private void installWorkshopMod(SteamTask steamTask)
    {
        WorkshopModInstallSteamTask task = (WorkshopModInstallSteamTask) steamTask;

        WorkshopMod workshopMod = this.steamWebApiService.getWorkshopMod(task.getFileId());
        InstalledModEntity installedModEntity = this.installedModRepository.findByWorkshopFileId(task.getFileId()).blockOptional().orElse(null);

        if (!shouldUpdateMod(workshopMod, installedModEntity, task.isForced()))
        {
            log.info("Mod {} up to date. No download needed.", task.getTitle());
            return;
        }

        Path steamCmdModFolderPath = downloadModFromWorkshop(task.getFileId(), task.getTitle());
        publishMessage(new WorkshopModInstallationStatus(task.getFileId(), 50));

        Path modDirectoryPath = null;
        if (SystemUtils.isWindows())
        {
            modDirectoryPath = this.modStorage.copyModFolderFromSteamCmd(steamCmdModFolderPath, Paths.get(this.aswgConfig.getServerDirectoryPath()), task.getTitle());
        }
        else
        {
            modDirectoryPath = this.modStorage.linkModFolderToSteamCmdModFolder(steamCmdModFolderPath, Paths.get(this.aswgConfig.getServerDirectoryPath()), task.getTitle());
        }
        publishMessage(new WorkshopModInstallationStatus(task.getFileId(), 75));

        saveModInDatabase(task.getFileId(), task.getTitle(), modDirectoryPath, workshopMod);
        publishMessage(new WorkshopModInstallationStatus(task.getFileId(), 100));
    }

    private boolean shouldUpdateMod(WorkshopMod workshopMod,
                                    InstalledModEntity installedModEntity,
                                    boolean forced)
    {
        if (forced)
            return true;

        if (installedModEntity == null)
            return true;

        if (workshopMod != null
            && (workshopMod.getLastUpdate().isEqual(installedModEntity.getLastWorkshopUpdate()) || workshopMod.getLastUpdate().isBefore(installedModEntity.getLastWorkshopUpdate())))
        {
            return false;
        }
        return true;
    }

    private void publishMessage(WorkshopModInstallationStatus status)
    {
        this.workshopModInstallProgressWebsocketHandler.publishInstallationStatus(status);
    }

    /**
     * Downloads the file and returns its path in the filesystem.
     *
     * @param fileId the id of the file to download.
     * @param title the title of the mod.
     * @return the path to the downloaded file.
     */
    private Path downloadModFromWorkshop(long fileId, String title) throws CouldNotDownloadWorkshopModException
    {
        String steamCmdPath = this.aswgConfig.getSteamCmdPath();
        if (!StringUtils.hasText(steamCmdPath))
            throw new SteamCmdPathNotSetException();

        Path path;
        try
        {
            path = downloadModThroughSteamCmd(SteamCmdWorkshopDownloadParameters.builder()
                    .fileId(fileId)
                    .title(title)
                    .appId(SteamUtils.ARMA_APP_ID)
                    .steamCmdPath(aswgConfig.getSteamCmdPath())
                    .steamUsername(aswgConfig.getSteamCmdUsername())
                    .steamPassword(aswgConfig.getSteamCmdPassword())
                    .build()).join();
        }
        catch (CompletionException e)
        {
            throw new CouldNotDownloadWorkshopModException(e.getMessage(), e);
        }

        if (path == null || Files.notExists(path))
        {
            throw new CouldNotDownloadWorkshopModException(format("Could not download mod id=%s title=%s.", fileId, title));
        }

        return path;
    }

    private void saveModInDatabase(long workshopFileId, String modName, Path modDirectory, WorkshopMod workshopMod)
    {
        InstalledModEntity installedModEntity = this.installedModRepository.findByWorkshopFileId(workshopFileId).block();
        MetaCppFile metaCppFile = null;
        try
        {
            metaCppFile = modStorage.readModMetaFile(modDirectory);
        }
        catch (CouldNotReadModMetaFile e)
        {
            throw new CouldNotInstallWorkshopModException(e.getMessage(), e);
        }

        InstalledModEntity.InstalledModEntityBuilder installedModBuilder;
        if (installedModEntity != null) // Update
        {
            log.info("Mod: {} already exists. Performing update only.", modName);
            installedModBuilder = installedModEntity.toBuilder();
        }
        else // New mod
        {
            installedModBuilder = InstalledModEntity.builder();
            installedModBuilder.createdDate(OffsetDateTime.now());
            installedModBuilder.lastWorkshopUpdate(workshopMod.getLastUpdate());
            installedModBuilder.workshopFileId(metaCppFile.getPublishedFileId());
        }

        installedModBuilder.name(Optional.ofNullable(metaCppFile.getName()).orElse(modName));
        installedModBuilder.directoryPath(modDirectory.toAbsolutePath().toString());

        try
        {
            if (workshopMod != null)
            {
                installedModBuilder.previewUrl(workshopMod.getPreviewUrl());
                installedModBuilder.lastWorkshopUpdate(workshopMod.getLastUpdate());
            }
        }
        catch (Exception exception)
        {
            log.warn("Could not fetch mod preview url. Mod = {}", modName, exception);
        }

        installedModRepository.save(installedModBuilder.build()).subscribe();
    }

    private CompletableFuture<Path> downloadModThroughSteamCmd(SteamCmdWorkshopDownloadParameters parameters)
    {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(Paths.get(parameters.getSteamCmdPath()).getParent().toFile());
        processBuilder.command(parameters.asExecutionParameters());
        Process process;
        try
        {
            log.info("Starting workshop mod download process with params: {}", parameters);
            process = processBuilder.start();
            handleProcessInputOutput(process);
            log.info("Download process started!");
        }
        catch (Exception e)
        {
            closeProcessInputOutput();
            return CompletableFuture.failedFuture(e);
        }
        return process.onExit().thenApplyAsync(p ->
                {
                    int exitValue = p.exitValue();
                    log.info("Exit value: " + exitValue);
                    closeProcessInputOutput();
                    if (exitValue == 0)
                    {
                        log.info("Mod download complete!");
                        return CompletableFuture.completedFuture("Ok!");
                    }
                    else
                    {
                        return CompletableFuture.failedFuture(new RuntimeException("Could not download the mod file! Exit value: " + exitValue));
                    }
                })
                .thenApplyAsync(t -> buildWorkshopModDownloadPath(parameters.getFileId()));
    }

    private Path buildWorkshopModDownloadPath(long fileId)
    {
        Path path;
        if (SystemUtils.isWindows())
        {
            path = buildSteamAppsPath(Paths.get(aswgConfig.getSteamCmdPath())
                    .getParent(), fileId);
        }
        else
        {
            path = buildSteamAppsPath(Paths.get(System.getProperty("user.home"))
                    .resolve("Steam"), fileId);

            if (!Files.exists(path))
            {
                path = buildSteamAppsPath(Paths.get(System.getProperty("user.home"))
                        .resolve(".local")
                        .resolve("share")
                        .resolve("Steam"), fileId);
            }
        }
        return path;
    }

    private Path buildSteamAppsPath(Path basePath, long fileId)
    {
        return basePath
                .resolve("steamapps")
                .resolve("workshop")
                .resolve("content")
                .resolve(String.valueOf(SteamUtils.ARMA_APP_ID))
                .resolve(String.valueOf(fileId));
    }

    private CompletableFuture<?> performArmaUpdate(SteamCmdAppUpdateParameters parameters)
    {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(Paths.get(parameters.getSteamCmdPath()).getParent().toFile());
        processBuilder.command(parameters.asExecutionParameters());
        Process process;
        try
        {
            log.info("Starting ARMA update process with params: {}", parameters);
            process = processBuilder.start();
            handleProcessInputOutput(process);
            log.info("Update started...");
        }
        catch (Exception e)
        {
            closeProcessInputOutput();
            return CompletableFuture.failedFuture(e);
        }
        return process.onExit().thenApplyAsync(p -> {
            int exitValue = p.exitValue();
            log.info("Exit value: " + exitValue);
            closeProcessInputOutput();
            if (exitValue == 0)
            {
                log.info("Arma update complete!");
                return CompletableFuture.completedFuture("Ok!");
            }
            else
            {
                return CompletableFuture.failedFuture(new RuntimeException("Could not update ARMA server! Exit value: " + exitValue));
            }
        });
    }

    private void handleProcessInputOutput(Process process)
    {
        this.steamCmdThread = new Thread(() ->
        {
            try {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
                reader.close();
            } catch (final Exception e) {
                e.printStackTrace();
                log.error("Error", e);
            }
        });

        this.steamCmdErrorThread = new Thread(() ->
        {
            try {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    log.error(line);
                }
                reader.close();
            } catch (final Exception e) {
                e.printStackTrace();
                log.error("Error", e);
            }
        });
        this.steamCmdErrorThread.setDaemon(true);
        this.steamCmdErrorThread.start();
        this.steamCmdThread.setDaemon(true);
        this.steamCmdThread.start();
    }

    private void closeProcessInputOutput()
    {
        if (steamCmdThread != null)
        {
            steamCmdThread.interrupt();
            steamCmdThread = null;
        }
        if (steamCmdErrorThread != null)
        {
            steamCmdErrorThread.interrupt();
            steamCmdErrorThread = null;
        }
    }
}
