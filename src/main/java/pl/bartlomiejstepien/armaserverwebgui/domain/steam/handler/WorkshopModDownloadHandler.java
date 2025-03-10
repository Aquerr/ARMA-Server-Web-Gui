package pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModFolderNameHelper;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.WorkshopModInstallProgressWebsocketHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.MetaCppFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModDirectory;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.SystemUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.dotnet.DotnetDateTimeUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamWebApiService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotDownloadWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotInstallWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamCmdPathNotSetException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamCmdWorkshopDownloadParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopModInstallSteamTask;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkshopModDownloadHandler implements SteamTaskHandler
{
    private final ASWGConfig aswgConfig;
    private final ModFolderNameHelper modFolderNameHelper;
    private final SteamWebApiService steamWebApiService;
    private final InstalledModRepository installedModRepository;
    private final ModStorage modStorage;
    private final WorkshopModInstallProgressWebsocketHandler workshopModInstallProgressWebsocketHandler;

    private Thread steamCmdThread;
    private Thread steamCmdErrorThread;

    @Override
    public void handle(SteamTask steamTask)
    {
        installWorkshopMod(steamTask);
    }

    private void publishMessage(WorkshopModInstallationStatus status)
    {
        this.workshopModInstallProgressWebsocketHandler.publishInstallationStatus(status);
    }

    private void installWorkshopMod(SteamTask steamTask)
    {
        WorkshopModInstallSteamTask task = (WorkshopModInstallSteamTask) steamTask;

        WorkshopMod workshopMod = this.steamWebApiService.getWorkshopMod(task.getFileId());
        InstalledModEntity installedModEntity = this.installedModRepository.findByWorkshopFileId(task.getFileId()).orElse(null);

        ModDirectory modDirectory = ModDirectory.from(buildModDirectoryPath(task.getTitle()));
        log.info("Prepared mod directory: {}", modDirectory.getPath());

        if (!shouldUpdateMod(modDirectory, workshopMod, installedModEntity, task.isForced()))
        {
            log.info("Mod {} up to date. No download needed.", task.getTitle());
            return;
        }

        Path steamCmdModFolderPath = downloadModFromWorkshop(task.getFileId(), task.getTitle());
        publishMessage(new WorkshopModInstallationStatus(task.getFileId(), 50));

        if (SystemUtils.isWindows())
        {
            this.modStorage.copyModFolderFromSteamCmd(steamCmdModFolderPath, modDirectory);
        }
        else
        {
            this.modStorage.linkModFolderToSteamCmdModFolder(steamCmdModFolderPath, modDirectory);
        }
        publishMessage(new WorkshopModInstallationStatus(task.getFileId(), 75));

        saveModInDatabase(task.getFileId(), task.getTitle(), modDirectory, workshopMod);
        publishMessage(new WorkshopModInstallationStatus(task.getFileId(), 100));
    }

    private Path buildModDirectoryPath(String modName)
    {
        return Paths.get(this.aswgConfig.getServerDirectoryPath()).resolve(this.aswgConfig.getModsDirectoryPath()).resolve(modFolderNameHelper.buildFor(modName)).normalize();
    }

    private boolean shouldUpdateMod(ModDirectory modDirectory,
                                    WorkshopMod workshopMod,
                                    InstalledModEntity installedModEntity,
                                    boolean forced)
    {
        if (forced)
            return true;

        if (installedModEntity == null)
            return true;

        if (Files.notExists(modDirectory.getPath()))
            return true;

        if (installedModEntity.getLastWorkshopUpdate() == null)
            return true;

        // If there is no way to determine if the mod is up to date.
        if (workshopMod == null)
            return true;

        log.info("WorkshopLastUpdate: {} | DB last update: {}", workshopMod.getLastUpdate(), installedModEntity.getLastWorkshopUpdate());

        // If we have equal or newer update time in db
        return workshopMod.getLastUpdate().isAfter(installedModEntity.getLastWorkshopUpdate());
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

    private void saveModInDatabase(long workshopFileId, String modName, ModDirectory modDirectory, WorkshopMod workshopMod)
    {
        InstalledModEntity installedModEntity = this.installedModRepository.findByWorkshopFileId(workshopFileId).orElse(null);

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

            long publishedFileIdToUse = ofNullable(modDirectory.getMetaCppFile())
                    .map(metaCppFile -> metaCppFile.getPublishedFileId() == 0 ? workshopFileId : metaCppFile.getPublishedFileId())
                    .orElseThrow(() -> new CouldNotInstallWorkshopModException("PublishedFileId cannot be 0"));

            installedModBuilder.workshopFileId(publishedFileIdToUse);
        }

        installedModBuilder.name(ofNullable(modDirectory.getMetaCppFile()).map(MetaCppFile::getName).orElse(modName));
        installedModBuilder.directoryPath(modDirectory.getPath().toAbsolutePath().toString());
        installedModBuilder.previewUrl(ofNullable(workshopMod).map(WorkshopMod::getPreviewUrl).orElse(null));
        installedModBuilder.lastWorkshopUpdate(ofNullable(workshopMod).map(WorkshopMod::getLastUpdate)
                .orElse(ofNullable(modDirectory.getMetaCppFile())
                        .map(MetaCppFile::getTimestamp)
                        .map(DotnetDateTimeUtils::dotnetTicksToOffsetDateTime)
                        .orElse(OffsetDateTime.now())));

        installedModRepository.saveAndFlush(installedModBuilder.build());
        log.info("Mod: {} saved in DB", modName);
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
