package pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.process.ExternalProcessHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModDirectory;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamWebApiService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotDownloadWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.SteamCmdPathNotSetException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.helper.SteamCmdModInstallHelper;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ModData;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamCmdWorkshopDownloadParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopModInstallSteamTask;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkshopModDownloadTaskHandler implements SteamTaskHandler
{
    private final ASWGConfig aswgConfig;
    private final SteamWebApiService steamWebApiService;
    private final InstalledModRepository installedModRepository;
    private final SteamCmdModInstallHelper steamCmdModInstallHelper;
    private final ExternalProcessHandler externalProcessHandler;

    @Override
    public void handle(SteamTask steamTask)
    {
        installWorkshopMod(steamTask);
    }

    private void installWorkshopMod(SteamTask steamTask)
    {
        WorkshopModInstallSteamTask task = (WorkshopModInstallSteamTask) steamTask;

        WorkshopMod workshopMod = this.steamWebApiService.getWorkshopMod(task.getFileId());
        InstalledModEntity installedModEntity = this.installedModRepository.findByWorkshopFileId(task.getFileId()).orElse(null);

        ModDirectory modDirectory = ModDirectory.from(steamCmdModInstallHelper.buildModDirectoryPath(Optional.ofNullable(installedModEntity)
                .map(InstalledModEntity::getModDirectoryName)
                .orElse(task.getTitle())));

        log.info("Prepared mod directory: {}", modDirectory.getPath());

        if (!steamCmdModInstallHelper.shouldUpdateMod(modDirectory, workshopMod, installedModEntity, task.isForced()))
        {
            log.info("Mod {} up to date. No download needed.", task.getTitle());
            return;
        }
        Path steamCmdModFolderPath = downloadModFromWorkshop(task.getFileId(), task.getTitle());

        ModData modData = ModData.builder()
                .fileId(task.getFileId())
                .title(task.getTitle())
                .workshopMod(workshopMod)
                .installedModEntity(installedModEntity)
                .modDirectory(modDirectory)
                .steamCmdModFolderPath(steamCmdModFolderPath)
                .build();

        steamCmdModInstallHelper.installDownloadedMod(modData);
    }

    /**
     * Downloads the file and returns its path in the filesystem.
     *
     * @param fileId the id of the file to download.
     * @param title  the title of the mod.
     * @return the path to the downloaded file.
     */
    private Path downloadModFromWorkshop(long fileId, String title) throws CouldNotDownloadWorkshopModException
    {
        String steamCmdPath = this.aswgConfig.getSteamCmdPath();
        if (!StringUtils.hasText(steamCmdPath))
            throw new SteamCmdPathNotSetException();

        try
        {
            downloadModThroughSteamCmd(SteamCmdWorkshopDownloadParameters.builder()
                    .fileId(fileId)
                    .title(title)
                    .appId(SteamUtils.ARMA_APP_ID)
                    .steamCmdPath(aswgConfig.getSteamCmdPath())
                    .steamUsername(aswgConfig.getSteamCmdUsername())
                    .steamPassword(aswgConfig.getSteamCmdPassword())
                    .build());
        }
        catch (Exception e)
        {
            throw new CouldNotDownloadWorkshopModException(e.getMessage(), e);
        }

        Path path = steamCmdModInstallHelper.buildWorkshopModDownloadPath(fileId);
        if (Files.notExists(path))
        {
            throw new CouldNotDownloadWorkshopModException(format("Could not download mod id=%s title=%s.", fileId, title));
        }
        return path;
    }

    private void downloadModThroughSteamCmd(SteamCmdWorkshopDownloadParameters parameters) throws Exception
    {
        externalProcessHandler.handle(Paths.get(parameters.getSteamCmdPath()).getParent().toFile(), parameters);
    }
}
