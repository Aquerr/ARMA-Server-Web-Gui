package pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.util.ExternalProcess;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotUpdateArmaServerException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamCmdPathNotSetException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamTaskHandleException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamCmdAppUpdateParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;

import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameUpdateTaskHandler implements SteamTaskHandler
{
    private final ASWGConfig aswgConfig;

    @Override
    public void handle(SteamTask steamTask)
    {
        String steamCmdPath = this.aswgConfig.getSteamCmdPath();
        if (!StringUtils.hasText(steamCmdPath))
            throw new SteamCmdPathNotSetException();
        try
        {
            performArmaUpdate(SteamCmdAppUpdateParameters.builder()
                    .appId(SteamUtils.ARMA_SERVER_APP_ID)
                    .branch(Optional.ofNullable(aswgConfig.getServerBranch()).filter(branch -> !SteamUtils.ARMA_BRANCH_PUBLIC.equals(branch))
                            .orElse(null))
                    .serverDirectoryPath(Paths.get(this.aswgConfig.getServerDirectoryPath()).toAbsolutePath().toString())
                    .steamCmdPath(steamCmdPath)
                    .steamUsername(this.aswgConfig.getSteamCmdUsername())
                    .steamPassword(this.aswgConfig.getSteamCmdPassword())
                    .build());
        }
        catch (Exception e)
        {
            throw new SteamTaskHandleException(new CouldNotUpdateArmaServerException(e.getMessage()));
        }
    }

    private void performArmaUpdate(SteamCmdAppUpdateParameters parameters) throws Exception
    {
        ExternalProcess externalProcess = new ExternalProcess();
        externalProcess.startProcess(Paths.get(parameters.getSteamCmdPath()).getParent().toFile(), parameters);
    }
}
