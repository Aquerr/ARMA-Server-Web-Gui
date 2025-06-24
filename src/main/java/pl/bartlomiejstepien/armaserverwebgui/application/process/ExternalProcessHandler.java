package pl.bartlomiejstepien.armaserverwebgui.application.process;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ExternalProcessHandler
{
    public void handle(File workingDirectory, ProcessParameters processParameters) throws IOException
    {
        ExternalProcess externalProcess = new ExternalProcess();
        externalProcess.startProcess(workingDirectory, processParameters);
    }
}
