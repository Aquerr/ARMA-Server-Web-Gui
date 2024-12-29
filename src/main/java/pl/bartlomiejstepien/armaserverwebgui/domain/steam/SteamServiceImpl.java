package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import com.github.koraktor.steamcondenser.steam.SteamPlayer;
import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamCmdNotInstalled;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.GameUpdateSteamTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopModInstallSteamTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamServiceImpl implements SteamService
{
    private static final String LOCALHOST_ADDRESS = "localhost";
    private static final int DEFAULT_SERVER_STEAM_QUERY_PORT = 2303;

    private final ASWGConfig aswgConfig;
    private final SteamWebApiService steamWebApiService;
    private final SteamCmdHandler steamCmdHandler;

    @Override
    public ArmaWorkshopQueryResponse queryWorkshopMods(WorkshopQueryParams params)
    {
        return steamWebApiService.queryWorkshopMods(params);
    }

    @Override
    public boolean isServerRunning()
    {
        try
        {
            GoldSrcServer goldSrcServer = new GoldSrcServer(LOCALHOST_ADDRESS, DEFAULT_SERVER_STEAM_QUERY_PORT);
            goldSrcServer.initialize();
            return true;
        }
        catch (Exception e)
        {
            log.info("Server is offline");
            return false;
        }
    }

    @Override
    public List<ArmaServerPlayer> getServerPlayers()
    {
        try
        {
            GoldSrcServer goldSrcServer = new GoldSrcServer(LOCALHOST_ADDRESS, DEFAULT_SERVER_STEAM_QUERY_PORT);
            goldSrcServer.initialize();
            return goldSrcServer.getPlayers().values().stream()
                    .map(this::mapToArmaServerPlayer)
                    .toList();
        }
        catch (Exception exception)
        {
            log.info("Could not get server players. Server seems to be offline. Reason: " + exception.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public UUID scheduleArmaUpdate()
    {
        if (!isSteamCmdInstalled())
            throw new SteamCmdNotInstalled();

        return this.steamCmdHandler.queueSteamTask(new GameUpdateSteamTask());
    }

    @Nullable
    @Override
    public WorkshopMod getWorkshopMod(long modId)
    {
        return steamWebApiService.getWorkshopMod(modId);
    }

    @Override
    public UUID scheduleWorkshopModDownload(long fileId, String title, boolean forced)
    {
        if (!isSteamCmdInstalled())
            throw new SteamCmdNotInstalled();

        return this.steamCmdHandler.queueSteamTask(new WorkshopModInstallSteamTask(fileId, title, forced));
    }

    @Override
    public boolean canUseWorkshop()
    {
        return isSteamCmdInstalled();
    }

    @Override
    public boolean isSteamCmdInstalled()
    {
        return !this.aswgConfig.getSteamCmdPath().isBlank() && Files.exists(Paths.get(this.aswgConfig.getSteamCmdPath()));
    }

    @Override
    public List<WorkshopModInstallationRequest> getInstallingMods()
    {
        return this.steamCmdHandler.getSteamTasks(SteamTask.Type.WORKSHOP_DOWNLOAD).stream()
                .map(WorkshopModInstallSteamTask.class::cast)
                .map(task -> new WorkshopModInstallationRequest(task.getFileId(), task.getTitle()))
                .toList();
    }

    @Override
    public boolean hasFinished(UUID taskId)
    {
        return this.steamCmdHandler.hasFinished(taskId);
    }

    private ArmaServerPlayer mapToArmaServerPlayer(SteamPlayer steamPlayer)
    {
        return ArmaServerPlayer.builder()
                .name(steamPlayer.getName())
                .build();
    }
}
