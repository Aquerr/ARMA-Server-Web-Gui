package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerStatus;

import java.util.List;

public interface ProcessService
{
    SseEmitter getServerLogPublisher();

    ServerStatus getServerStatus();

    void startServer(boolean performUpdate);

    void stopServer();

    List<ArmaServerPlayer> getServerPlayers();

    List<String> getLatestServerLogs();
}
