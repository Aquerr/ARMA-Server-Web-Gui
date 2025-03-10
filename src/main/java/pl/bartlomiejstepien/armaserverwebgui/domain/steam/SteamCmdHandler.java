package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotDownloadWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotInstallWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotUpdateArmaServerException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.RetryableException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamCmdPathNotSetException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamTaskHandleException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.SteamTaskHandler;
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
import static java.util.Optional.ofNullable;

@Component
@Slf4j
@RequiredArgsConstructor
public class SteamCmdHandler
{
    private static final Deque<QueuedSteamTask> STEAM_TASKS = new ConcurrentLinkedDeque<>();
    private final SteamTaskRetryPolicy steamTaskRetryPolicy;
    private final WorkshopModInstallProgressWebsocketHandler workshopModInstallProgressWebsocketHandler;
    private final Map<SteamTask.Type, SteamTaskHandler> steamTaskHandlers;

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
        steamTaskHandlers.get(steamTask.getType()).handle(steamTask);
    }

    private void publishMessage(WorkshopModInstallationStatus status)
    {
        this.workshopModInstallProgressWebsocketHandler.publishInstallationStatus(status);
    }
}
