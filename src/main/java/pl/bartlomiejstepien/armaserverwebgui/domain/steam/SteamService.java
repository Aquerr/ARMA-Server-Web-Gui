package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;

import java.util.List;

public interface SteamService
{
    ArmaWorkshopQueryResponse queryWorkshopMods(WorkshopQueryParams params);

    boolean isServerRunning();

    List<ArmaServerPlayer> getServerPlayers();

    boolean updateArma();
}
