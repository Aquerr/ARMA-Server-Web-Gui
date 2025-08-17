package pl.bartlomiejstepien.armaserverwebgui.application.process;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamCmdExternalProcess;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;

@Component
public class ExternalProcessHandler
{
    public void handle(File workingDirectory, ProcessParameters processParameters, ExternalProcessType externalProcessType) throws IOException
    {
        if (externalProcessType == ExternalProcessType.STEAMCMD)
        {
            new SteamCmdExternalProcess().startProcess(workingDirectory, processParameters);
            return;
        }
        throw new UnsupportedOperationException(format("Process type '%s' not supported!", externalProcessType.name()));
    }
}
