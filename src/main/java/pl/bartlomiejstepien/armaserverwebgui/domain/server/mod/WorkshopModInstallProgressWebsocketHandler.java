package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationStatus;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@RequiredArgsConstructor
@Component
public class WorkshopModInstallProgressWebsocketHandler implements WebSocketHandler
{
    private final ObjectMapper objectMapper;

    private final Sinks.Many<WorkshopModInstallationStatus> workShopModInstallationSink = Sinks.many().multicast().onBackpressureBuffer();

    public void publishInstallationStatus(WorkshopModInstallationStatus status)
    {
        this.workShopModInstallationSink.tryEmitNext(status);
    }

    @Override
    public Mono<Void> handle(WebSocketSession session)
    {
        return session.send(workShopModInstallationSink.asFlux().map(status -> convertToWebSocketMessage(session, status)));
    }

    private WebSocketMessage convertToWebSocketMessage(WebSocketSession session, WorkshopModInstallationStatus status)
    {
        try
        {
            String message = objectMapper.writeValueAsString(status);
            return session.textMessage(message);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
