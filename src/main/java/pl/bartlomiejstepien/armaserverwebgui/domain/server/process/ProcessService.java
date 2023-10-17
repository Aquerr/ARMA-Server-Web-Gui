package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import org.reactivestreams.Publisher;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerStatus;

import java.util.List;

public interface ProcessService
{
    Publisher<String> getServerLogPublisher();

    ServerStatus getServerStatus();

    void startServer(boolean performUpdate);

    void stopServer();

    List<ArmaServerPlayer> getServerPlayers();
}
