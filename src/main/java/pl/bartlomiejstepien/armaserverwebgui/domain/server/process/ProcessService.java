package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import org.reactivestreams.Publisher;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerStatus;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProcessService
{
    Publisher<String> getServerLogPublisher();

    ServerStatus getServerStatus();

    Mono<Void> startServer(boolean performUpdate);

    Mono<Void> stopServer();

    List<ArmaServerPlayer> getServerPlayers();

    List<String> getLatestServerLogs();
}
