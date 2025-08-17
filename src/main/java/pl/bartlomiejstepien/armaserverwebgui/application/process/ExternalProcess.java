package pl.bartlomiejstepien.armaserverwebgui.application.process;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public abstract class ExternalProcess
{
    protected Process process;

    private Thread inputStreamThread;
    private Thread errorStreamThread;

    protected final Deque<String> logs = new ConcurrentLinkedDeque<>();

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
            handleHealthCheck();
        }
        catch (Exception exception)
        {
            closeInputOutput();
            postClose();
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
            throw new IOException(exception);
        }
        finally
        {
            closeInputOutput();
            postClose();
        }
    }

    protected abstract void postClose();

    protected abstract void handleHealthCheck();

    public void closeInputOutput()
    {
        closeProcessInputOutput();
    }

    private void handleProcessInputOutput()
    {
        this.inputStreamThread = Thread.ofVirtual().start(() ->
        {
            try(Scanner scanner = new Scanner(process.getInputStream()))
            {
                while (scanner.hasNextLine())
                {
                    addLog(scanner.nextLine(), false);
                }
            }
            catch (final Exception e)
            {
                log.error("Error", e);
            }
        });

        this.errorStreamThread = Thread.ofVirtual().start(() ->
        {
            try(Scanner scanner = new Scanner(process.getErrorStream()))
            {
                while (scanner.hasNextLine())
                {
                    addLog(scanner.nextLine(), true);
                }
            }
            catch (final Exception e)
            {
                log.error("Error", e);
            }
        });
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

        this.logs.clear();
    }

    private void addLog(String line, boolean isError)
    {
        if (isError)
            log.error(line);
        else
            log.info(line);

        logs.add(line);
        if (logs.size() > 5)
            logs.removeFirst();
    }
}
