package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.DiscordIntegration;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordWebhookMessageParams;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception.ServerIsAlreadyRunningException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ArmaServerParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessServiceImpl implements ProcessService
{
    private static final Logger SERVER_LOGGER = LoggerFactory.getLogger("arma-server");
    private static final String PID_FILE_NAME = "arma_server.pid";

    private final SteamService steamService;
    private final ArmaServerParametersGenerator serverParametersGenerator;
    private final Optional<DiscordIntegration> discordIntegration;

    private final ASWGConfig aswgConfig;

    @Value("${server.log.file.name}")
    private String serverLogFileName;

    private boolean serverStartScheduled;
    private boolean isUpdating;

    private Thread serverThread;
    private Thread ioServerThread;
    private Thread ioServerErrorThread;

    private final Sinks.Many<String> serverLogSink = Sinks.many().multicast().directAllOrNothing();

    @Override
    public Publisher<String> getServerLogPublisher()
    {
        return serverLogSink.asFlux();
    }

    @Override
    public ServerStatus getServerStatus()
    {
        if (isUpdating)
            return ServerStatus.of(ServerStatus.Status.UPDATING, "Updating");
        else if (serverStartScheduled)
            return ServerStatus.of(ServerStatus.Status.STARTING, "Starting");
        else if (this.steamService.isServerRunning())
            return ServerStatus.of(ServerStatus.Status.ONLINE, "Online");
        long pid;
        try
        {
            pid = getServerPid();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not get server pid.", e);
        }
        return pid != 0 ? ServerStatus.of(ServerStatus.Status.RUNNING_BUT_NOT_DETECTED_BY_STEAM, "Running but not detected by Steam")
                : ServerStatus.of(ServerStatus.Status.OFFLINE, "Offline");
    }

    @Override
    public Mono<Void> startServer(boolean performUpdate)
    {
        if (getServerStatus().getStatus() != ServerStatus.Status.OFFLINE || serverStartScheduled)
            throw new ServerIsAlreadyRunningException("Server is already running!");

        serverStartScheduled = true;

        Mono<?> flow = Mono.empty();
        if (performUpdate)
        {
            flow = flow
                    .then(trySendDiscordMessage(DiscordWebhookMessageParams.builder().title("Updating server").build()))
                    .then(Mono.fromRunnable(this::tryUpdateArmaServer));
        }

        return flow.thenMany(Flux.merge(
                trySendDiscordMessage(DiscordWebhookMessageParams.builder().title("Server started").build()),
                serverParametersGenerator.generateParameters()
                        .flatMap(this::startServerProcess)
        )).last();
    }

    private Mono<Void> trySendDiscordMessage(DiscordWebhookMessageParams params)
    {
        return Mono.just(discordIntegration)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(di -> di.sendMessage(params));
    }

    private Mono<Void> startServerProcess(ArmaServerParameters serverParameters)
    {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(serverParameters.getServerDirectory()));
        processBuilder.command(serverParameters.asList());

        log.info("Starting server process with params: {}", serverParameters.asList());

        try
        {
            clearLogsFile();
        }
        catch (IOException exception)
        {
            log.warn("Could not clear " + serverLogFileName);
        }

        this.serverThread = new Thread(() -> {
            try
            {
                Process process = processBuilder.start();

                long pid = process.pid();
                log.info("Process started: {}", pid);

                saveServerPid(pid);
                handleProcessInputOutput(process);
                serverStartScheduled = false;
            }
            catch (IOException e)
            {
                serverStartScheduled = false;
                log.error("Error", e);
                stopServer();
            }
        });
        this.serverThread.setDaemon(true);
        this.serverThread.start();

        return Mono.empty();
    }

    private void clearLogsFile() throws IOException
    {
        new PrintWriter(getServerLogsFile()).close();
    }

    private void tryUpdateArmaServer()
    {
        this.isUpdating = true;
        try
        {
            if (steamService.isSteamCmdInstalled())
            {
                UUID taskId = steamService.scheduleArmaUpdate();

                // Wait for update
                while (true)
                {
                    if (!steamService.hasFinished(taskId))
                    {
                        Thread.sleep(2000);
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            this.isUpdating = false;
        }
    }

    @Override
    public Mono<Void> stopServer()
    {
        long pid;
        try
        {
            pid = getServerPid();
            log.info("Found server pid={}", pid);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not get server pid.", e);
        }


        ProcessHandle.of(pid).ifPresent(processHandle -> {
            processHandle.destroy();
            processHandle.onExit().thenAccept(processHandle1 -> {
                log.info("Server process stopped for pid={}", processHandle1.pid());
            });

            if (this.ioServerThread != null)
            {
                this.ioServerThread.interrupt();
                this.ioServerThread = null;
            }
            if (this.ioServerErrorThread != null)
            {
                this.ioServerErrorThread.interrupt();
                this.ioServerErrorThread = null;
            }
            if (this.serverThread != null)
            {
                this.serverThread.interrupt();
                this.serverThread = null;
            }
        });

        try
        {
            saveServerPid(0);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not save server pid.", e);
        }
        return trySendDiscordMessage(DiscordWebhookMessageParams.builder()
                .title("Server Stopped")
                .build());
    }

    @Override
    public List<ArmaServerPlayer> getServerPlayers()
    {
        return this.steamService.getServerPlayers();
    }

    @Override
    public List<String> getLatestServerLogs()
    {
        try
        {
            File serverLogsFile = getServerLogsFile();
            if (!serverLogsFile.exists()) {
                return Collections.emptyList();
            }
            return Files.readAllLines(getServerLogsFile().toPath());
        }
        catch (IOException e)
        {
            log.warn("Could not fetch server logs.", e);
            return Collections.emptyList();
        }
    }

    private void handleProcessInputOutput(Process process)
    {
        this.ioServerThread = new Thread(() ->
        {
            try {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                    SERVER_LOGGER.info(line);
                    serverLogSink.tryEmitNext(line);
                }
                reader.close();
            } catch (final Exception e) {
                e.printStackTrace();
                log.error("Error", e);
            }
        });

        this.ioServerErrorThread = new Thread(() ->
        {
            try {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                    SERVER_LOGGER.info(line);
                    serverLogSink.tryEmitNext(line);
                }
                reader.close();
            } catch (final Exception e) {
                e.printStackTrace();
                log.error("Error", e);
            }
        });
        this.ioServerErrorThread.setDaemon(true);
        this.ioServerErrorThread.start();
        this.ioServerThread.setDaemon(true);
        this.ioServerThread.start();
    }

    private void saveServerPid(long pid) throws IOException
    {
        File pidFile = getPidFile();
        pidFile.createNewFile();
        try(FileWriter fileWriter = new FileWriter(pidFile, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter))
        {
            printWriter.print(pid);
        }
    }

    private int getServerPid() throws IOException
    {
        File pidFile = getPidFile();
        if (!pidFile.exists())
            return 0;
        try(FileReader fileReader = new FileReader(pidFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            Scanner scanner = new Scanner(bufferedReader))
        {
            return scanner.nextInt();
        }
    }

    private File getPidFile()
    {
        return new File(aswgConfig.getServerDirectoryPath() + File.separator + PID_FILE_NAME);
    }

    private File getServerLogsFile()
    {
        return new File(aswgConfig.getServerDirectoryPath() + File.separator + serverLogFileName);
    }
}
