package pl.bartlomiejstepien.armaserverwebgui.service;

import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerPlayer;

import java.util.List;

public interface SteamService
{
    boolean isServerRunning();

    List<ArmaServerPlayer> getServerPlayers();

    boolean updateArma();
}
