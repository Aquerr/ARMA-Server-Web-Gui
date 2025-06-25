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
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ModDownloadResult;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamCmdWorkshopBatchDownloadParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopBatchModDownloadTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkshopBatchModDownloadTaskHandler implements SteamTaskHandler
{
    private final ASWGConfig aswgConfig;
    private final SteamWebApiService steamWebApiService;
    private final InstalledModRepository installedModRepository;
    private final SteamCmdModInstallHelper steamCmdModInstallHelper;
    private final ExternalProcessHandler externalProcessHandler;

    @Override
    public void handle(SteamTask steamTask)
    {
        handleBatchModDownload(steamTask);
    }

    private void handleBatchModDownload(SteamTask steamTask)
    {
        WorkshopBatchModDownloadTask task = (WorkshopBatchModDownloadTask) steamTask;

        List<ModData> modsToUpdate = calculateModsToUpdate(task.getFileIdsWithTitles(), task.isForced());
        ModDownloadResult modDownloadResult = downloadModsFromWorkshop(modsToUpdate);
        Map<Long, Path> successDownloadMods = modDownloadResult.getSuccessMods();
        modsToUpdate = modsToUpdate.stream().filter(modData -> successDownloadMods.containsKey(modData.getFileId()))
                .peek(modData -> modData.setSteamCmdModFolderPath(successDownloadMods.get(modData.getFileId()).toAbsolutePath().normalize()))
                .toList();

        for (ModData modData : modsToUpdate)
        {
            steamCmdModInstallHelper.installDownloadedMod(modData);
        }

        if (!modDownloadResult.getFailedMods().isEmpty())
        {
            // To retry the steam task
            throw new CouldNotDownloadWorkshopModException("Couldn't download workshop mods: "
                    + Arrays.toString(modDownloadResult.getFailedMods().keySet().stream().map(String::valueOf).toArray()));
        }
    }

    private List<ModData> calculateModsToUpdate(Map<Long, String> fileIdsWithTitles, boolean forced)
    {
        List<ModData> modsToUpdate = new ArrayList<>(fileIdsWithTitles.size());

        List<Long> fileIds = fileIdsWithTitles.keySet().stream().toList();
        Map<Long, WorkshopMod> workshopMods = this.steamWebApiService.getWorkshopMods(fileIds).stream()
                .collect(Collectors.toMap(WorkshopMod::getFileId, workshopMod -> workshopMod));

        Map<Long, InstalledModEntity> installedModEntities = this.installedModRepository.findAllByWorkshopFileIdIn(fileIds)
                .stream()
                .collect(Collectors.toMap(InstalledModEntity::getWorkshopFileId, installedModEntity -> installedModEntity));

        Map<Long, ModDirectory> modDirectories = fileIdsWithTitles.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        longStringEntry -> ModDirectory.from(steamCmdModInstallHelper.buildModDirectoryPath(
                                Optional.ofNullable(installedModEntities.get(longStringEntry.getKey()))
                                        .map(InstalledModEntity::getModDirectoryName)
                                        .orElse(longStringEntry.getValue())))));

        for (Map.Entry<Long, String> entry : fileIdsWithTitles.entrySet())
        {
            long fileId = entry.getKey();
            String title = entry.getValue();
            ModDirectory modDirectory = modDirectories.get(entry.getKey());
            InstalledModEntity installedModEntity = installedModEntities.get(entry.getKey());
            WorkshopMod workshopMod = workshopMods.get(entry.getKey());

            if (!steamCmdModInstallHelper.shouldUpdateMod(modDirectory, workshopMod, installedModEntity, forced))
            {
                log.info("Mod {} up to date. No download needed.", title);
                continue;
            }
            modsToUpdate.add(ModData.builder()
                    .fileId(fileId)
                    .title(title)
                    .modDirectory(modDirectory)
                    .installedModEntity(installedModEntity)
                    .workshopMod(workshopMod)
                    .build());
        }

        return modsToUpdate;
    }

    /**
     * Downloads the file and returns its path in the filesystem.
     *
     * @param modsToInstall the mods to download.
     */
    private ModDownloadResult downloadModsFromWorkshop(List<ModData> modsToInstall) throws CouldNotDownloadWorkshopModException
    {
        String steamCmdPath = this.aswgConfig.getSteamCmdPath();
        if (!StringUtils.hasText(steamCmdPath))
            throw new SteamCmdPathNotSetException();

        try
        {
            downloadModThroughSteamCmd(SteamCmdWorkshopBatchDownloadParameters.builder()
                    .fileIds(modsToInstall.stream().map(ModData::getFileId).toList())
                    .titles(modsToInstall.stream().map(ModData::getTitle).toList())
                    .appId(SteamUtils.ARMA_APP_ID)
                    .steamCmdPath(aswgConfig.getSteamCmdPath())
                    .steamUsername(aswgConfig.getSteamCmdUsername())
                    .steamPassword(aswgConfig.getSteamCmdPassword())
                    .build());
        }
        catch (Exception e)
        {
            log.warn("Failed to batch download mods from workshop", e);
        }

        Map<Long, Path> successMods = new HashMap<>();
        Map<Long, Path> failedMods = new HashMap<>();

        for (ModData modData : modsToInstall)
        {
            Path path = steamCmdModInstallHelper.buildWorkshopModDownloadPath(modData.getFileId());
            if (Files.exists(path))
            {
                successMods.put(modData.getFileId(), path);
            }
            else
            {
                log.warn(format("Could not download mod id=%s title=%s.", modData.getFileId(), modData.getTitle()));
                failedMods.put(modData.getFileId(), path);
            }
        }

        return new ModDownloadResult(successMods, failedMods);
    }

    private void downloadModThroughSteamCmd(SteamCmdWorkshopBatchDownloadParameters parameters) throws Exception
    {
        externalProcessHandler.handle(Paths.get(parameters.getSteamCmdPath()).getParent().toFile(), parameters);
    }
}
