package pl.bartlomiejstepien.armaserverwebgui.service;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.SteamPlayer;
import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.exception.SteamCmdPathNotSetException;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerPlayer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SteamServiceImpl implements SteamService
{
    private static final String LOCALHOST_ADDRESS = "localhost";
    private static final int DEFAULT_SERVER_STEAM_QUERY_PORT = 2303;

    private final ASWGConfig aswgConfig;

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
