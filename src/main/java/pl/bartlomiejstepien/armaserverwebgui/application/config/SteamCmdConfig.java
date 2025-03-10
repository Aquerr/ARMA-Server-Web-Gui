package pl.bartlomiejstepien.armaserverwebgui.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.GameUpdateHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.SteamTaskHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler.WorkshopModDownloadHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;

import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
public class SteamCmdConfig
{
    @Bean
    public Map<SteamTask.Type, SteamTaskHandler> steamTaskHandlers(
            GameUpdateHandler gameUpdateHandler,
            WorkshopModDownloadHandler workshopModDownloadHandler)
    {
        Map<SteamTask.Type, SteamTaskHandler> handlers = new HashMap<>();
        handlers.put(SteamTask.Type.GAME_UPDATE, gameUpdateHandler);
        handlers.put(SteamTask.Type.WORKSHOP_DOWNLOAD, workshopModDownloadHandler);
        return handlers;
    }
}
