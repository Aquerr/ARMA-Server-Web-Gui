package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import com.github.koraktor.steamcondenser.steam.SteamPlayer;
import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import io.github.aquerr.steamwebapiclient.SteamWebApiClient;
import io.github.aquerr.steamwebapiclient.request.PublishedFileDetailsRequest;
import io.github.aquerr.steamwebapiclient.request.WorkShopQueryFilesRequest;
import io.github.aquerr.steamwebapiclient.response.PublishedFileDetailsResponse;
import io.github.aquerr.steamwebapiclient.response.WorkShopQueryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.SystemUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotDownloadWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamCmdPathNotSetException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamServiceImpl implements SteamService
{
    private static final Integer ARMA_APP_ID = 107410;

    private static final String LOCALHOST_ADDRESS = "localhost";
    private static final int DEFAULT_SERVER_STEAM_QUERY_PORT = 2303;

    private final ASWGConfig aswgConfig;
    private final SteamWebApiClient steamWebApiClient;
    private final ArmaWorkshopModConverter armaWorkshopModConverter;

    private Thread ioDownloadThread;
    private Thread ioDownloadErrorThread;

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
        catch (Exception e)
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
        return Optional.ofNullable(this.steamWebApiClient.getSteamRemoteStorageClient().getPublishedFileDetails(new PublishedFileDetailsRequest(List.of(modId))))
                .map(PublishedFileDetailsResponse::getResponse)
                .map(PublishedFileDetailsResponse.QueryFilesResponse::getPublishedFileDetails)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .map(this.armaWorkshopModConverter::convert)
                .orElse(null);
    }

    /**
     * Downloads the file and returns its path in the filesystem.
     *
     * @param fileId the id of the file to download.
     * @return the path to the downloaded file.
     */
    @Override
    public Path downloadModFromWorkshop(long fileId) throws CouldNotDownloadWorkshopModException
    {
        String steamCmdPath = this.aswgConfig.getSteamCmdPath();
        if (!StringUtils.hasText(steamCmdPath))
            throw new SteamCmdPathNotSetException();
        try
        {
            Path path = downloadModThroughSteamCmd(steamCmdPath, fileId).join();
            if (Files.notExists(path))
            {
                throw new CouldNotDownloadWorkshopModException("Could not download mod file.");
            }
            return path;
        }
        catch (CompletionException e)
        {
            throw new CouldNotDownloadWorkshopModException(e.getMessage(), e);
        }
    }

    private CompletableFuture<Path> downloadModThroughSteamCmd(String steamCmdPath, long fileId)
    {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(Paths.get(steamCmdPath).getParent().toFile());
        processBuilder.command(steamCmdPath,
                "+login", aswgConfig.getSteamCmdUsername(), aswgConfig.getSteamCmdPassword(),
                "+workshop_download_item", String.valueOf(ARMA_APP_ID), String.valueOf(fileId),
                "+quit");
        Process process = null;
        try
        {
            log.info("Starting workshop mod download process with params: {}", processBuilder.command());
            process = processBuilder.start();
            handleDownloadProcessInputOutput(process);
            log.info("Download process started!");
        }
        catch (Exception e)
        {
            closeDownloadProcessInputOutput();
            return CompletableFuture.failedFuture(e);
        }
        return process.onExit().thenApplyAsync(p ->
            {
                int exitValue = p.exitValue();
                log.info("Exit value: " + exitValue);
                closeDownloadProcessInputOutput();
                if (exitValue == 0)
                {
                    log.info("Mod download complete!");
                    return CompletableFuture.completedFuture("Ok!");
                }
                else
                {
                    return CompletableFuture.failedFuture(new RuntimeException("Could not download the mod file! Exit value: " + exitValue));
                }
            })
            .thenApplyAsync(t -> buildWorkshopModDownloadPath(fileId));
    }

    private Path buildWorkshopModDownloadPath(long fileId)
    {
        Path path;
        if (SystemUtils.isWindows())
        {
            path = buildSteamAppsPath(Paths.get(aswgConfig.getSteamCmdPath())
                    .getParent(), fileId);
        }
        else
        {
            path = buildSteamAppsPath(Paths.get(System.getProperty("user.home"))
                    .resolve("Steam"), fileId);

            if (!Files.exists(path))
            {
                path = buildSteamAppsPath(Paths.get(System.getProperty("user.home"))
                        .resolve(".local")
                        .resolve("share")
                        .resolve("Steam"), fileId);
            }
        }
        return path;
    }

    private Path buildSteamAppsPath(Path basePath, long fileId)
    {
        return basePath
                .resolve("steamapps")
                .resolve("workshop")
                .resolve("content")
                .resolve(String.valueOf(ARMA_APP_ID))
                .resolve(String.valueOf(fileId));
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

    private void handleDownloadProcessInputOutput(Process process)
    {
        this.ioDownloadThread = new Thread(() ->
        {
            try {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
                reader.close();
            } catch (final Exception e) {
                e.printStackTrace();
                log.error("Error", e);
            }
        });

        this.ioDownloadErrorThread = new Thread(() ->
        {
            try {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
                reader.close();
            } catch (final Exception e) {
                e.printStackTrace();
                log.error("Error", e);
            }
        });
        this.ioDownloadErrorThread.setDaemon(true);
        this.ioDownloadErrorThread.start();
        this.ioDownloadThread.setDaemon(true);
        this.ioDownloadThread.start();
    }

    private void closeDownloadProcessInputOutput()
    {
        if (ioDownloadThread != null)
        {
            ioDownloadThread.interrupt();
            ioDownloadThread = null;
        }
        if (ioDownloadErrorThread != null)
        {
            ioDownloadErrorThread.interrupt();
            ioDownloadErrorThread = null;
        }
    }
}
