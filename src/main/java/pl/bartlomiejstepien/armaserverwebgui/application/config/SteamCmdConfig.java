package pl.bartlomiejstepien.armaserverwebgui.application.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.GameUpdateTaskHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.SteamTaskHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.WorkshopModDownloadTaskHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;

@Configuration(proxyBeanMethods = false)
public class SteamCmdConfig
{
    @Bean
    public Map<SteamTask.Type, SteamTaskHandler> steamTaskHandlers(
            GameUpdateTaskHandler gameUpdateTaskHandler,
            WorkshopModDownloadTaskHandler workshopModDownloadTaskHandler)
    {
        Map<SteamTask.Type, SteamTaskHandler> handlers = new HashMap<>();
        handlers.put(SteamTask.Type.GAME_UPDATE, gameUpdateTaskHandler);
        handlers.put(SteamTask.Type.WORKSHOP_DOWNLOAD, workshopModDownloadTaskHandler);
        return handlers;
    }
}
