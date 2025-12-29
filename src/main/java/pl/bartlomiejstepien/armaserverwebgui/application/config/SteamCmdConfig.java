package pl.bartlomiejstepien.armaserverwebgui.application.config;

import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.GameUpdateTaskHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.SteamTaskHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.WorkshopBatchModDownloadTaskHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.WorkshopModDownloadTaskHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;

import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
public class SteamCmdConfig
{
    @Bean
    public Map<SteamTask.Type, SteamTaskHandler> steamTaskHandlers(
            GameUpdateTaskHandler gameUpdateTaskHandler,
            WorkshopModDownloadTaskHandler workshopModDownloadTaskHandler,
            WorkshopBatchModDownloadTaskHandler workshopBatchModDownloadTaskHandler)
    {
        Map<SteamTask.Type, SteamTaskHandler> handlers = new HashMap<>();
        handlers.put(SteamTask.Type.GAME_UPDATE, gameUpdateTaskHandler);
        handlers.put(SteamTask.Type.WORKSHOP_DOWNLOAD, workshopModDownloadTaskHandler);
        handlers.put(SteamTask.Type.WORKSHOP_BATCH_DOWNLOAD, workshopBatchModDownloadTaskHandler);
        return handlers;
    }

    @Bean
    public TaskScheduler steamCmdTaskScheduler()
    {
        return new ThreadPoolTaskSchedulerBuilder()
                .poolSize(1)
                .build();
    }
}
