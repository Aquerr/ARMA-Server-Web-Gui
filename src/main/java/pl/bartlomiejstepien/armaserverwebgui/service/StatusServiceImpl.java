package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.exception.ServerIsAlreadyRunningException;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerParameters;
import pl.bartlomiejstepien.armaserverwebgui.model.ServerStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusServiceImpl implements StatusService
{
    private final SteamService steamService;
    private final ArmaServerParametersGenerator serverParametersGenerator;
    private long lastServerPid;

    private boolean serverStartScheduled;

    @Override
    public ServerStatus getServerStatus()
    {
        return this.steamService.isServerRunning() ? ServerStatus.ONLINE : ServerStatus.OFFLINE;
    }

    @Override
    public boolean startServer()
    {
        if (getServerStatus() == ServerStatus.ONLINE || serverStartScheduled || lastServerPid != 0)
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

            log.info("Process started: {}", process.pid());
            this.lastServerPid = process.pid();

            final Thread ioThread = new Thread() {
                @Override
                public void run() {
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
                }
            };
            ioThread.start();
            serverStartScheduled = false;
            return true;
        }
        catch (IOException e)
        {
            serverStartScheduled = false;
            log.error("Error", e);
            stopServer();
            return false;
        }
    }

    @Override
    public boolean stopServer()
    {
        ProcessHandle.of(lastServerPid).ifPresent(processHandle -> {
            processHandle.destroy();
            lastServerPid = 0;
            processHandle.onExit().thenAccept(processHandle1 -> {
                log.info("Server process stopped for pid={}", processHandle1.pid());
            });
        });

        if (lastServerPid == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
