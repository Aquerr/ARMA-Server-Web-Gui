package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SteamService
{
    ArmaWorkshopQueryResponse queryWorkshopMods(WorkshopQueryParams params);

    boolean isServerRunning();

    List<ArmaServerPlayer> getServerPlayers();

    UUID scheduleArmaUpdate();

    WorkshopMod getWorkshopMod(long modId);

    UUID scheduleWorkshopModDownload(long fileId, String title, boolean forced);

    UUID scheduleWorkshopModDownload(Map<Long, String> fileIdsWithTitles, boolean forced);

    boolean canUseWorkshop();

    boolean isSteamCmdInstalled();

    List<WorkshopModInstallationRequest> getInstallingMods();

    boolean hasFinished(UUID taskId);
}
