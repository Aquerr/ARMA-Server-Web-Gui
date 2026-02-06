package pl.bartlomiejstepien.armaserverwebgui.domain.steam.helper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModFolderNameHelper;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.WorkshopModInstallProgressWebsocketHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.MetaCppFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModDirectory;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModFileStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.FileUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.SystemUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.dotnet.DotnetDateTimeUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotInstallWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ModData;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;

import static java.util.Optional.ofNullable;

@Slf4j
@Component
@AllArgsConstructor
public class SteamCmdModInstallHelper
{
    private final ASWGConfig aswgConfig;
    private final ModFolderNameHelper modFolderNameHelper;
    private final ModFileStorage modFileStorage;
    private final InstalledModRepository installedModRepository;
    private final WorkshopModInstallProgressWebsocketHandler workshopModInstallProgressWebsocketHandler;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void installDownloadedMod(ModData modData)
    {
        ModDirectory modDirectory = modData.getModDirectory();
        Path steamCmdModFolderPath = modData.getSteamCmdModFolderPath();

        publishMessage(new WorkshopModInstallationStatus(modData.getFileId(), 50));

        if (SystemUtils.isWindows())
        {
            this.modFileStorage.copyModFolderFromSteamCmd(steamCmdModFolderPath, modDirectory);
        }
        else
        {
            // If mod directory exists (because it was uploaded manually) then we replace it with steam folder to make updates easier.
            if (Files.exists(modDirectory.getPath().toAbsolutePath()) && !Files.isSymbolicLink(modDirectory.getPath().toAbsolutePath()))
            {
                FileUtils.deleteFilesRecursively(modDirectory.getPath().toAbsolutePath(), false);
            }

            this.modFileStorage.linkModFolderToSteamCmdModFolder(steamCmdModFolderPath, modDirectory);
        }
        publishMessage(new WorkshopModInstallationStatus(modData.getFileId(), 75));

        saveModInDatabase(modData.getFileId(), modDirectory.getModName(), modDirectory, modData.getWorkshopMod());
        publishMessage(new WorkshopModInstallationStatus(modData.getFileId(), 100));
    }

    private void publishMessage(WorkshopModInstallationStatus status)
    {
        this.workshopModInstallProgressWebsocketHandler.publishInstallationStatus(status);
    }

    @Transactional
    public void saveModInDatabase(long workshopFileId, String modName, ModDirectory modDirectory, WorkshopMod workshopMod)
    {
        InstalledModEntity installedModEntity = this.installedModRepository.findByWorkshopFileId(workshopFileId).orElse(null);

        InstalledModEntity.InstalledModEntityBuilder installedModBuilder;
        if (installedModEntity != null) // Update
        {
            log.info("Mod: {} already exists. Performing update only.", modName);
            installedModBuilder = installedModEntity.toBuilder();
        }
        else // New mod
        {
            installedModBuilder = InstalledModEntity.builder();
            installedModBuilder.createdDate(OffsetDateTime.now());

            long publishedFileIdToUse = ofNullable(modDirectory.getMetaCppFile())
                    .map(metaCppFile -> metaCppFile.getPublishedFileId() == 0 ? workshopFileId : metaCppFile.getPublishedFileId())
                    .orElseThrow(() -> new CouldNotInstallWorkshopModException("PublishedFileId cannot be 0"));

            installedModBuilder.workshopFileId(publishedFileIdToUse);
        }

        installedModBuilder.name(modName);
        installedModBuilder.directoryPath(modDirectory.getPath().toAbsolutePath().toString());
        installedModBuilder.previewUrl(ofNullable(workshopMod).map(WorkshopMod::getPreviewUrl).orElse(null));
        installedModBuilder.lastWorkshopUpdateDate(ofNullable(workshopMod).map(WorkshopMod::getLastUpdate)
                .orElse(ofNullable(modDirectory.getMetaCppFile())
                        .map(MetaCppFile::getTimestamp)
                        .map(DotnetDateTimeUtils::dotnetTicksToOffsetDateTime)
                        .orElse(OffsetDateTime.now())));
        installedModBuilder.lastWorkshopUpdateAttemptDate(OffsetDateTime.now());
        installedModBuilder.dependenciesIds(ofNullable(workshopMod).map(WorkshopMod::getDependencies).orElse(List.of()));

        installedModRepository.saveAndFlush(installedModBuilder.build());
        log.info("Mod: {} saved in DB", modName);
    }

    public Path buildWorkshopModDownloadPath(long fileId)
    {
        String customWorkshopContentPath = this.aswgConfig.getSteamCmdWorkshopContentPath();
        if (StringUtils.hasText(customWorkshopContentPath))
            return resolveWorkshopModPath(Paths.get(customWorkshopContentPath), fileId);

        if (SystemUtils.isWindows())
        {
            return buildSteamAppsPath(Paths.get(aswgConfig.getSteamCmdPath())
                    .getParent(), fileId);
        }
        else
        {
            String userHomeDirectory = System.getProperty("user.home");
            Path path = buildSteamAppsPath(Paths.get(userHomeDirectory)
                    .resolve("Steam"), fileId);

            if (Files.exists(path))
                return path;

            return buildSteamAppsPath(Paths.get(userHomeDirectory)
                    .resolve(".local")
                    .resolve("share")
                    .resolve("Steam"), fileId);
        }
    }

    private Path buildSteamAppsPath(Path basePath, long fileId)
    {
        return resolveWorkshopModPath(basePath
                .resolve("steamapps")
                .resolve("workshop")
                .resolve("content"), fileId);
    }

    private Path resolveWorkshopModPath(Path basePath, long fileId)
    {
        return basePath
                .resolve(String.valueOf(SteamUtils.ARMA_APP_ID))
                .resolve(String.valueOf(fileId));
    }

    public Path buildModDirectoryPath(String modName)
    {
        return Paths.get(this.aswgConfig.getServerDirectoryPath())
                .resolve(this.aswgConfig.getModsDirectoryPath())
                .resolve(modFolderNameHelper.buildFor(modName))
                .normalize();
    }

    public boolean shouldUpdateMod(ModDirectory modDirectory,
                                    WorkshopMod workshopMod,
                                    InstalledModEntity installedModEntity,
                                    boolean forced)
    {
        if (forced)
            return true;

        if (installedModEntity == null)
            return true;

        if (Files.notExists(modDirectory.getPath()))
            return true;

        if (installedModEntity.getLastWorkshopUpdateDate() == null)
            return true;

        // If there is no way to determine if the mod is up to date.
        if (workshopMod == null)
            return true;

        log.info("WorkshopLastUpdate: {} | DB last update: {}", workshopMod.getLastUpdate(), installedModEntity.getLastWorkshopUpdateDate());

        // If we have equal or newer update time in db
        return workshopMod.getLastUpdate().isAfter(installedModEntity.getLastWorkshopUpdateDate());
    }
}
