package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationStatus;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Component
public class WorkshopModInstallProgressWebsocketHandler extends TextWebSocketHandler
{
    private static final Cache<String, WebSocketSession> websocketCache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(30))
            .build();

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        websocketCache.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception
    {
        websocketCache.invalidate(session.getId());
    }

    public void publishInstallationStatus(WorkshopModInstallationStatus status)
    {
        for (WebSocketSession session : websocketCache.asMap().values())
        {
            TextMessage textMessage = convertToWebSocketMessage(status);
            try
            {
                session.sendMessage(textMessage);
            }
            catch (IOException e)
            {
                log.error("Could not send message '{}' to websocket '{}'", textMessage, session, e);
            }
        }
    }

    private TextMessage convertToWebSocketMessage(WorkshopModInstallationStatus status)
    {
        try
        {
            String message = objectMapper.writeValueAsString(status);
            return new TextMessage(message);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
