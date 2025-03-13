package pl.bartlomiejstepien.armaserverwebgui.application.util;

import lombok.extern.slf4j.Slf4j;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ProcessParameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class ExternalProcess
{
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
            log.info("Staring process with params: {}", processParameters);
            this.process = processBuilder.start();
            log.info("Process started!");
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
            try {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
                reader.close();
            } catch (final Exception e) {
                log.error("Error", e);
            }
        });

        this.errorStreamThread = new Thread(() ->
        {
            try {
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    log.error(line);
                }
                reader.close();
            } catch (final Exception e) {
                log.error("Error", e);
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
}
