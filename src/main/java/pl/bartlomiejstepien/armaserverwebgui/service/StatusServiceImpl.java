package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.exception.ServerIsAlreadyRunningException;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerParameters;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.model.ServerStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusServiceImpl implements StatusService
{
    private static final String PID_FILE_NAME = "arma_server.pid";

    private final SteamService steamService;
    private final ArmaServerParametersGenerator serverParametersGenerator;

    private final ASWGConfig aswgConfig;

    private boolean serverStartScheduled;

    @Override
    public ServerStatus getServerStatus()
    {
        if (serverStartScheduled)
            return ServerStatus.STARTING;
        if (this.steamService.isServerRunning())
            return ServerStatus.ONLINE;
        long pid;
        try
        {
            pid = getServerPid();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not get server pid.", e);
        }
        return pid != 0 ? ServerStatus.ONLINE : ServerStatus.OFFLINE;
    }

    @Override
    public void startServer()
    {
        if (getServerStatus() != ServerStatus.OFFLINE || serverStartScheduled)
            throw new ServerIsAlreadyRunningException("Server is already running!");

        serverStartScheduled = true;

        try
        {
            ArmaServerParameters serverParams = serverParametersGenerator.generateParameters();

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(serverParams.getServerDirectory()));
            processBuilder.command(serverParams.asList());

            log.info("Starting server process with params: {}", serverParams.asList());
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

        ProcessHandle.of(pid).ifPresent(processHandle -> {
            processHandle.destroy();
            processHandle.onExit().thenAccept(processHandle1 -> {
                log.info("Server process stopped for pid={}", processHandle1.pid());
            });

            try
            {
                saveServerPid(processHandle.pid());
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not save server pid.", e);
            }
        });
    }

    @Override
    public List<ArmaServerPlayer> getServerPlayers()
    {
        return this.steamService.getServerPlayers();
    }

    private void handleProcessInputOutput(Process process)
    {
        final Thread ioThread = new Thread(() ->
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
        ioThread.start();
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
}
