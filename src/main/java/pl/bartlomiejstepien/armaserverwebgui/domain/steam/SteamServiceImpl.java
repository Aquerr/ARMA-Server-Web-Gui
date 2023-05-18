package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.SteamPlayer;
import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import io.github.aquerr.steamwebapiclient.SteamWebApiClient;
import io.github.aquerr.steamwebapiclient.request.WorkShopQueryFilesRequest;
import io.github.aquerr.steamwebapiclient.response.WorkShopQueryResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamCmdPathNotSetException;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SteamServiceImpl implements SteamService
{
    private static final Integer ARMA_APP_ID = 107410;

    private static final String LOCALHOST_ADDRESS = "localhost";
    private static final int DEFAULT_SERVER_STEAM_QUERY_PORT = 2303;

    private final ASWGConfig aswgConfig;
    private final SteamWebApiClient steamWebApiClient;
    private final ArmaWorkshopModConverter armaWorkshopModConverter;

    @Override
    public ArmaWorkshopQueryResponse queryWorkshopMods(WorkshopQueryParams params) {
        WorkShopQueryResponse workShopQueryResponse = steamWebApiClient.getWorkshopWebApiClient().queryFiles(WorkShopQueryFilesRequest.builder()
                .appId(ARMA_APP_ID)
                .cursor(StringUtils.hasText(params.getCursor()) ? params.getCursor() : "*")
                .numPerPage(10)
                .searchText(StringUtils.hasText(params.getSearchText()) ? params.getSearchText() : null)
                .returnPreviews(true)
                .queryType(WorkShopQueryFilesRequest.PublishedFileQueryType.RANKED_BY_TOTAL_UNIQUE_SUBSCRIPTIONS)
                .fileType(WorkShopQueryFilesRequest.PublishedFileInfoMatchingFileType.ITEMS)
                .build());

        String nextPageCursor = null;
        List<ArmaWorkshopMod> armaWorkshopMods = Collections.emptyList();
        if (workShopQueryResponse != null)
        {
            nextPageCursor = workShopQueryResponse.getResponse().getNextCursor();
            armaWorkshopMods = workShopQueryResponse.getResponse().getPublishedFileDetails().stream()
                    .map(armaWorkshopModConverter::convert)
                    .toList();
        }

        return ArmaWorkshopQueryResponse.builder()
                .nextCursor(nextPageCursor)
                .mods(armaWorkshopMods)
                .build();
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
        catch (SteamCondenserException | TimeoutException e)
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
                    .collect(Collectors.toList());
        }
        catch (SteamCondenserException | TimeoutException e)
        {
            log.info("Could not get server players. Server seems to be offline.");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean updateArma()
    {
        String steamCmdPath = this.aswgConfig.getSteamCmdPath();
        if (!StringUtils.hasText(steamCmdPath))
            throw new SteamCmdPathNotSetException();

        performArmaUpdate(steamCmdPath, this.aswgConfig.getServerDirectoryPath());

        return false;
    }

    @Override
    public ArmaWorkshopMod getWorkshopMod(long modId)
    {
        return null;
    }

    private void performArmaUpdate(String steamCmdPath, String serverDirectoryPath)
    {
        //TODO: We need to inform GUI about the installation status!
        //TODO: Currently, not working... anonymous user cannot download arma server files.
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(steamCmdPath,
                "+force_install_dir", serverDirectoryPath,
                "+login", "anonymous",
                "+app_update", "233780", "validate",
                "+quit");

        try
        {
            Process process = processBuilder.start();
            process.onExit().thenAccept(p -> {
                log.info("Arma server update completed!");
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private ArmaServerPlayer mapToArmaServerPlayer(SteamPlayer steamPlayer)
    {
        return ArmaServerPlayer.builder()
                .name(steamPlayer.getName())
                .build();
    }
}
