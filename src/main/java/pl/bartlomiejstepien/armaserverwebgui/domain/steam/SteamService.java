package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;

import java.util.List;

public interface SteamService
{
    boolean isServerRunning();

    List<ArmaServerPlayer> getServerPlayers();

    boolean updateArma();
}
