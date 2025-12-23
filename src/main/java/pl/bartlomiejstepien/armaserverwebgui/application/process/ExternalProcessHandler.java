package pl.bartlomiejstepien.armaserverwebgui.application.process;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;

import java.io.File;
import java.io.IOException;

@Component
@AllArgsConstructor
public class ExternalProcessHandler
{
    private final ASWGConfig aswgConfig;

    public void handle(File workingDirectory, ProcessParameters processParameters) throws IOException
    {
        ExternalProcess externalProcess = new ExternalProcess(aswgConfig);
        externalProcess.startProcess(workingDirectory, processParameters);
    }
}
