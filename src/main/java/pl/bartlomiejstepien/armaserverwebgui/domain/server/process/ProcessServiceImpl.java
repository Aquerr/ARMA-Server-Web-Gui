package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AuthenticationFacade;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.DiscordIntegration;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.MessageKind;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception.ServerIsAlreadyRunningException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception.ServerNotInstalledException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.log.ServerProcessLogMessageObserver;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ArmaServerParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerProcessStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
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
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessServiceImpl implements ProcessService
{
    private static final String PID_FILE_NAME = "arma_server.pid";

    private final AuthenticationFacade authenticationFacade;
    private final SteamService steamService;
    private final ArmaServerParametersGenerator serverParametersGenerator;
    private final DiscordIntegration discordIntegration;
    private final ProcessAliveChecker processAliveChecker;

    private final ASWGConfig aswgConfig;

    private final List<ServerProcessLogMessageObserver> logMessageObservers;

    @Value("${aswg.logs.location}")
    private String logsLocation;

    @Value("${server.log.file.name}")
    private String serverLogFileName;

    private volatile ServerProcessStatus processStatus;

    private final ReentrantLock serverStartUpLock = new ReentrantLock();

    private Thread serverThread;
    private Thread ioServerThread;
    private Thread ioServerErrorThread;

    public ServerProcessStatus getProcessStatus()
    {
        if (processStatus == ServerProcessStatus.UPDATING)
            return processStatus;
        else if (processStatus == ServerProcessStatus.STARTING)
            return processStatus;

        long pid;
        try
        {
            pid = getServerPid();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not get server pid.", e);
        }

        if (pid != 0 && processAliveChecker.isPidAlive(pid))
            return ServerProcessStatus.RUNNING;
        return ServerProcessStatus.NOT_RUNNING;
    }

    @Override
    public void startServer(boolean performUpdate)
    {
        if (this.getProcessStatus() != ServerProcessStatus.NOT_RUNNING)
            throw new ServerIsAlreadyRunningException("Server is already running!");

        if (serverStartUpLock.isLocked())
            throw new ServerIsAlreadyRunningException("Server is starting!");

        serverStartUpLock.lock();

        try
        {
            updateStatus(ServerProcessStatus.STARTING);

            if (performUpdate)
            {
                sendDiscordMessage(MessageKind.SERVER_UPDATED);
                tryUpdateArmaServer();
            }

            sendDiscordMessage(MessageKind.SERVER_STARTED);
            startServerProcess();
            serverStartUpLock.unlock();
            //TODO: Start server healthcheck (status monitoring) here.
        }
        catch (Exception exception)
        {
            updateStatus(ServerProcessStatus.NOT_RUNNING);
            serverStartUpLock.unlock();
            throw exception;
        }
    }

    private void updateStatus(ServerProcessStatus processStatus)
    {
        this.processStatus = processStatus;
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
                updateStatus(ServerProcessStatus.RUNNING);
            }
            catch (IOException e)
            {
                updateStatus(ServerProcessStatus.NOT_RUNNING);
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
            updateStatus(ServerProcessStatus.UPDATING);
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
            updateStatus(ServerProcessStatus.STARTING);
        }
    }

    @Override
    public void stopServer()
    {
        long pid;
        try
        {
            pid = getServerPid();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not get server pid.", e);
        }

        log.info("Found server pid={}", pid);

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
            updateStatus(ServerProcessStatus.NOT_RUNNING);
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
                    handleProcessLog(line);
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
                    handleProcessLog(line);
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

    private void handleProcessLog(String line)
    {
        for (ServerProcessLogMessageObserver observer : this.logMessageObservers)
        {
            try
            {
                observer.handleServerLogMessage(line);
            }
            catch (Exception exception)
            {
                log.warn("Log observer produced an error", exception);
            }
        }
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
}
