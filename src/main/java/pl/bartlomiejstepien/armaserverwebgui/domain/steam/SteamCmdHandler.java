package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.WorkshopModInstallProgressWebsocketHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.RetryableException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.SteamTaskHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.QueuedSteamTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopBatchModDownloadTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopModInstallSteamTask;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

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
        return STEAM_TASKS.stream().noneMatch(task -> task.getId().equals(taskId));
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
            this.currentlyProcessingTask = null;
            log.warn("Exception during handling of steam task.", exception);
            if (exception instanceof RetryableException && steamTaskRetryPolicy.canRetry(queuedSteamTask))
            {
                log.info("Requeuing steam task: {}", queuedSteamTask);
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
        else if (queuedSteamTask.getSteamTask().getType() == SteamTask.Type.WORKSHOP_DOWNLOAD)
        {
            publishMessage(new WorkshopModInstallationStatus(((WorkshopModInstallSteamTask) queuedSteamTask.getSteamTask()).getFileId(), 0));
        }
        else
        {
            ((WorkshopBatchModDownloadTask) queuedSteamTask.getSteamTask())
                    .getFileIdsWithTitles()
                    .keySet()
                    .forEach(id -> publishMessage(new WorkshopModInstallationStatus(id, 0)));
        }
        STEAM_TASKS.add(queuedSteamTask);
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
