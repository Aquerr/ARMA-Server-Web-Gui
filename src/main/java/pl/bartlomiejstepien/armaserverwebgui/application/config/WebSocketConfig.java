package pl.bartlomiejstepien.armaserverwebgui.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.WorkshopModInstallProgressWebsocketHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig
{
    @Bean
    public HandlerMapping handlerMapping(WorkshopModInstallProgressWebsocketHandler workshopModInstallProgressWebsocketHandler)
    {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/api/v1/ws/workshop-mod-install-progress", workshopModInstallProgressWebsocketHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(1);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter()
    {
        return new WebSocketHandlerAdapter();
    }
}
