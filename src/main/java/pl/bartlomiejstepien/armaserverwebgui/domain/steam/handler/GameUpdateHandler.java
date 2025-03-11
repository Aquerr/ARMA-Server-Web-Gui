package pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotUpdateArmaServerException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamCmdPathNotSetException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamTaskHandleException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamCmdAppUpdateParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameUpdateHandler implements SteamTaskHandler
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
                    .serverDirectoryPath(this.aswgConfig.getServerDirectoryPath())
                    .steamCmdPath(steamCmdPath)
                    .steamUsername(this.aswgConfig.getSteamCmdUsername())
                    .steamPassword(this.aswgConfig.getSteamCmdPassword())
                    .build()).join();
        }
        catch (CompletionException e)
        {
            throw new SteamTaskHandleException(new CouldNotUpdateArmaServerException(e.getMessage()));
        }
    }

    private CompletableFuture<?> performArmaUpdate(SteamCmdAppUpdateParameters parameters)
    {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(Paths.get(parameters.getSteamCmdPath()).getParent().toFile());
        processBuilder.command(parameters.asExecutionParameters());
        processBuilder.inheritIO();
        Process process;
        try
        {
            log.info("Starting ARMA update process with params: {}", parameters);
            process = processBuilder.start();
            log.info("Update started...");
        }
        catch (Exception e)
        {
            return CompletableFuture.failedFuture(e);
        }
        return process.onExit().thenApplyAsync(p -> {
            int exitValue = p.exitValue();
            log.info("Exit value: " + exitValue);
            if (exitValue == 0)
            {
                log.info("Arma update complete!");
                return CompletableFuture.completedFuture("Ok!");
            }
            else
            {
                return CompletableFuture.failedFuture(new RuntimeException("Could not update ARMA server! Exit value: " + exitValue));
            }
        });
    }
}
