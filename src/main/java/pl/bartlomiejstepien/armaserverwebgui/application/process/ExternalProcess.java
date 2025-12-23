package pl.bartlomiejstepien.armaserverwebgui.application.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@RequiredArgsConstructor
public class ExternalProcess
{
    private static final Logger STEAMCMD_LOGGER = LoggerFactory.getLogger("steamcmd");

    private final ASWGConfig aswgConfig;

    private Process process;

    private Thread inputStreamThread;
    private Thread errorStreamThread;

    public void startProcess(File workingDirectory,
                             ProcessParameters processParameters) throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(workingDirectory);
        processBuilder.command(processParameters.asProcessParameters());

        try
        {
            logInfo("Staring process with params: {}", processParameters);
            this.process = processBuilder.start();
            logInfo("Process started!");
            handleProcessInputOutput();
        }
        catch (Exception exception)
        {
            closeInputOutput();
            throw new IOException(exception);
        }

        try
        {
            this.process.onExit()
                    .thenApplyAsync(Process::exitValue)
                    .thenApplyAsync(exitCode ->
                    {
                        if (exitCode == 0)
                        {
                            return true;
                        }
                        else
                        {
                            throw new RuntimeException("Exit code: " + exitCode);
                        }
                    }).join();
        }
        catch (Exception exception)
        {
            closeInputOutput();
            throw new IOException(exception);
        }
    }

    public void closeInputOutput()
    {
        closeProcessInputOutput();
    }

    private void handleProcessInputOutput()
    {
        this.inputStreamThread = new Thread(() ->
        {
            try
            {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    logInfo(line);
                }
                reader.close();
            }
            catch (final Exception e)
            {
                logError("Error", e);
            }
        });

        this.errorStreamThread = new Thread(() ->
        {
            try
            {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    logError(line);
                }
                reader.close();
            }
            catch (final Exception e)
            {
                logError("Error", e);
            }
        });
        this.errorStreamThread.setDaemon(true);
        this.errorStreamThread.start();
        this.inputStreamThread.setDaemon(true);
        this.inputStreamThread.start();
    }

    private void closeProcessInputOutput()
    {
        if (inputStreamThread != null)
        {
            inputStreamThread.interrupt();
            inputStreamThread = null;
        }
        if (errorStreamThread != null)
        {
            errorStreamThread.interrupt();
            errorStreamThread = null;
        }
    }

    private void logInfo(String message, Object... args)
    {
        log.info(message, args);
        if (aswgConfig.isSteamCmdlogToServerConsole())
        {
            STEAMCMD_LOGGER.info(message, args);
        }
    }

    private void logError(String message, Object... args)
    {
        log.error(message, args);
        if (aswgConfig.isSteamCmdlogToServerConsole())
        {
            STEAMCMD_LOGGER.error(message, args);
        }
    }

    private void logError(String message, Throwable throwable)
    {
        log.error(message, throwable);
        if (aswgConfig.isSteamCmdlogToServerConsole())
        {
            STEAMCMD_LOGGER.error(message, throwable);
        }
    }
}
