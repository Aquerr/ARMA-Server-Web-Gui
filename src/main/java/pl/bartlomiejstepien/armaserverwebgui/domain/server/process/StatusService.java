package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ServerStatus;

import java.util.List;

public interface StatusService
{
    ServerStatus getServerStatus();

    void startServer();

    void stopServer();

    List<ArmaServerPlayer> getServerPlayers();
}
