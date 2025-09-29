package pl.bartlomiejstepien.armaserverwebgui.application.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.WorkshopModInstallProgressWebsocketHandler;

@Configuration(proxyBeanMethods = false)
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer
{
    private final WorkshopModInstallProgressWebsocketHandler workshopModInstallProgressWebsocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        registry.addHandler(
                workshopModInstallProgressWebsocketHandler,
                "/api/v1/ws/workshop-mod-install-progress"
        ).setAllowedOrigins("*");
    }
}
