package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaWorkshopQueryResponse;

import java.util.List;

public interface SteamService
{
    ArmaWorkshopQueryResponse queryWorkshopMods(String cursor);

    boolean isServerRunning();

    List<ArmaServerPlayer> getServerPlayers();

    boolean updateArma();
}
