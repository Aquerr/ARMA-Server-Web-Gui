package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotDownloadWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotUpdateArmaServerException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;

import java.nio.file.Path;
import java.util.List;

public interface SteamService
{
    ArmaWorkshopQueryResponse queryWorkshopMods(WorkshopQueryParams params);

    boolean isServerRunning();

    List<ArmaServerPlayer> getServerPlayers();

    boolean updateArma() throws CouldNotUpdateArmaServerException;

    ArmaWorkshopMod getWorkshopMod(long modId);

    Path downloadModFromWorkshop(long fileId) throws CouldNotDownloadWorkshopModException;

    boolean canUseWorkshop();

    boolean isSteamCmdInstalled();
}
