package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerStatus;

public interface ProcessService
{
    SseEmitter getServerLogEmitter();

    ServerStatus getServerStatus();

    void startServer(boolean performUpdate);

    void stopServer();

    List<ArmaServerPlayer> getServerPlayers();

    List<String> getLatestServerLogs();
}
