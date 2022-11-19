package pl.bartlomiejstepien.armaserverwebgui.service;

import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.model.ServerStatus;

import java.util.List;

public interface StatusService
{
    ServerStatus getServerStatus();

    boolean startServer();

    boolean stopServer();

    List<ArmaServerPlayer> getServerPlayers();
}
