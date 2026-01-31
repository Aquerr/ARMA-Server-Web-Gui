package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AuthenticationFacade;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.DiscordIntegration;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.MessageKind;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception.ServerIsAlreadyRunningException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception.ServerNotInstalledException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ArmaServerParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserDetails;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessServiceImpl implements ProcessService
{
    private static final Logger SERVER_LOGGER = LoggerFactory.getLogger("arma-server");
    private static final String PID_FILE_NAME = "arma_server.pid";

    private final AuthenticationFacade authenticationFacade;
    private final SteamService steamService;
    private final ArmaServerParametersGenerator serverParametersGenerator;
    private final DiscordIntegration discordIntegration;
    private final ProcessAliveChecker processAliveChecker;

    private final ASWGConfig aswgConfig;

    @Value("${aswg.logs.location}")
    private String logsLocation;

    @Value("${server.log.file.name}")
    private String serverLogFileName;

    private volatile ServerStatus.Status serverStatus;

    private final ReentrantLock serverStartUpLock = new ReentrantLock();

    private Thread serverThread;
    private Thread ioServerThread;
    private Thread ioServerErrorThread;

    private static final ConcurrentLinkedDeque<SseEmitter> serverLogsEmitters = new ConcurrentLinkedDeque<>();
    private static final ScheduledExecutorService SERVER_LOGS_EMITTER_HEALTHCHECK_SERVICE = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void postConstruct()
    {
        SERVER_LOGS_EMITTER_HEALTHCHECK_SERVICE.scheduleAtFixedRate(this::performServerLogsEmittersHealthcheck, 5, 10, TimeUnit.SECONDS);
    }

    private void performServerLogsEmittersHealthcheck()
    {
        try
        {
            serverLogsEmitters.forEach(emitter ->
            {
                try
                {
                    emitter.send(SseEmitter.event().name("ping").data("healthcheck"));
                }
                catch (IOException ex)
                {
                    log.warn("SseEmitter did not respond to ping event. Connection will be closed.", ex);
                    emitter.complete(); //Not needed... but just in case...
                }
            });
        }
        catch (Exception exception)
        {
            log.warn("Could not perform SseEmitters healthcheck. Reason: {}", exception.getMessage());
        }
    }

    @Override
    public SseEmitter getServerLogEmitter()
    {
        SseEmitter emitter = new SseEmitter(0L);
        emitter.onTimeout(() ->
        {
            serverLogsEmitters.remove(emitter);
            log.info("SseEmitter timeout");
            emitter.complete();
        });
        emitter.onError(throwable ->
        {
            serverLogsEmitters.remove(emitter);
            log.warn("SseEmitter error", throwable);
            emitter.complete();
        });
        emitter.onCompletion(() -> serverLogsEmitters.remove(emitter));
        serverLogsEmitters.add(emitter);
        return emitter;
    }

    @Override
    public ServerStatus getServerStatus()
    {
        //TODO: Get status from server healthcheck (status monitoring) here.

        log.info("Fetching server status...");
        if (ServerStatus.Status.UPDATING == serverStatus)
            return ServerStatus.of(ServerStatus.Status.UPDATING, "Updating");
        else if (ServerStatus.Status.STARTING == serverStatus)
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

        if (pid == 0)
            return ServerStatus.of(ServerStatus.Status.OFFLINE, "Offline");
        else if (processAliveChecker.isPidAlive(pid))
            return ServerStatus.of(ServerStatus.Status.RUNNING_BUT_NOT_DETECTED_BY_STEAM, "Running but not detected by Steam");
        return ServerStatus.of(ServerStatus.Status.OFFLINE, "Offline");
    }

    @Override
    public void startServer(boolean performUpdate)
    {
        if (getServerStatus().getStatus() != ServerStatus.Status.OFFLINE)
            throw new ServerIsAlreadyRunningException("Server is already running!");

        if (serverStartUpLock.isLocked())
        {
            throw new ServerIsAlreadyRunningException("Server is starting!");
        }

        serverStartUpLock.lock();

        try
        {
            serverStatus = ServerStatus.Status.STARTING;

            if (performUpdate)
            {
                sendDiscordMessage(MessageKind.SERVER_UPDATED);
                tryUpdateArmaServer();
            }

            sendDiscordMessage(MessageKind.SERVER_STARTED);
            serverStatus = ServerStatus.Status.STARTING;
            startServerProcess();
            serverStartUpLock.unlock();
            //TODO: Start server healthcheck (status monitoring) here.
        }
        catch (Exception exception)
        {
            serverStatus = ServerStatus.Status.OFFLINE;
            serverStartUpLock.unlock();
            throw exception;
        }
    }

    private void sendDiscordMessage(MessageKind messageKind)
    {
        discordIntegration.sendMessage(messageKind);
    }

    private void startServerProcess()
    {
        ArmaServerParameters serverParameters = serverParametersGenerator.generateParameters();
        Path serverExecutablePath = Paths.get(serverParameters.getExecutablePath());
        if (Files.notExists(serverExecutablePath))
            throw new ServerNotInstalledException(String.format("Server executable '%s' does not exist. Is the server installed?",
                    serverExecutablePath.toAbsolutePath()));

        log.info("Server Directory: {}", serverParameters.getServerDirectory());
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(Paths.get(".").resolve(serverParameters.getServerDirectory()).toFile());
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

        this.serverThread = new Thread(() ->
        {
            try
            {
                Process process = processBuilder.start();

                long pid = process.pid();
                log.info("Process started: {}", pid);

                saveServerPid(pid);
                handleProcessInputOutput(process);
                this.serverStatus = ServerStatus.Status.ONLINE;
            }
            catch (IOException e)
            {
                this.serverStatus = ServerStatus.Status.OFFLINE;
                log.error("Error", e);
                stopServer();
            }
        });
        this.serverThread.setDaemon(true);
        this.serverThread.start();
    }

    private void clearLogsFile() throws IOException
    {
        new PrintWriter(getServerLogsFile()).close();
    }

    private void tryUpdateArmaServer()
    {
        try
        {
            this.serverStatus = ServerStatus.Status.UPDATING;
            if (steamService.isSteamCmdInstalled())
            {
                log.info("Scheduling Arma update");
                UUID taskId = steamService.scheduleArmaUpdate(authenticationFacade.getCurrentUser().map(AswgUserDetails::getUsername).orElse(null));

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
            this.serverStatus = ServerStatus.Status.STARTING;
        }
    }

    @Override
    public void stopServer()
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


        ProcessHandle.of(pid).ifPresent(processHandle ->
        {
            processHandle.destroy();
            processHandle.onExit().thenAccept(processHandle1 ->
            {
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
            this.serverStatus = ServerStatus.Status.OFFLINE;
        });

        try
        {
            saveServerPid(0);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not save server pid.", e);
        }
        sendDiscordMessage(MessageKind.SERVER_STOPPED);
    }

    @Override
    public List<ArmaServerPlayer> getServerPlayers()
    {
        log.info("Fetching server players...");
        return this.steamService.getServerPlayers();
    }

    @Override
    public List<String> getLatestServerLogs()
    {
        try
        {
            File serverLogsFile = getServerLogsFile();
            if (!serverLogsFile.exists())
            {
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
            try
            {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    SERVER_LOGGER.info(line);
                    CompletableFuture.runAsync(new SubmitLogTask(line));
                }
                reader.close();
            }
            catch (final Exception e)
            {
                e.printStackTrace();
                log.error("Error", e);
            }
        });

        this.ioServerErrorThread = new Thread(() ->
        {
            try
            {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    SERVER_LOGGER.info(line);
                    CompletableFuture.runAsync(new SubmitLogTask(line));
                }
                reader.close();
            }
            catch (final Exception e)
            {
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
        try (FileWriter fileWriter = new FileWriter(pidFile, false);
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
        try (FileReader fileReader = new FileReader(pidFile);
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
        return Paths.get(this.logsLocation).resolve(serverLogFileName).toFile();
    }

    @lombok.Value
    private static class SubmitLogTask implements Runnable
    {
        String log;

        @Override
        public void run()
        {
            emitSseLog(log);
        }

        private void emitSseLog(String line)
        {
            serverLogsEmitters.forEach(emitter ->
            {
                try
                {
                    emitter.send(line);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
