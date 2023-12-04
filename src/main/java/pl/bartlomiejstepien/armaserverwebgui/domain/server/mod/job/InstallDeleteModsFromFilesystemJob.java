package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.InstalledFileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@Slf4j
public class InstallDeleteModsFromFilesystemJob
{
    private final ASWGConfig aswgConfig;
    private final ModService modService;
    private final SteamService steamService;

    @Scheduled(fixedDelay = 1L, timeUnit = TimeUnit.HOURS)
    public void scanModDirectories()
    {
        if (!this.aswgConfig.isFileScannerDeletionEnabled() && !this.aswgConfig.isFileScannerInstallationEnabled())
        {
            log.info("File scanner job is disabled. Skipping...");
            return;
        }

        List<InstalledFileSystemMod> installedFileSystemMods = modService.getInstalledModsFromFileSystem();
        saveOrDeleteModsFromDB(installedFileSystemMods);
    }

    private void saveOrDeleteModsFromDB(List<InstalledFileSystemMod> installedFileSystemMods)
    {
        modService.getInstalledMods()
                .collectList()
                .map(installedModsInDB -> {
                    installNewMods(installedModsInDB, installedFileSystemMods);
                    deleteOldMods(installedModsInDB, installedFileSystemMods);
                    return installedModsInDB;
                }).subscribe();
    }

    private void deleteOldMods(List<InstalledModEntity> installedModsInDB, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        if (!aswgConfig.isFileScannerDeletionEnabled())
        {
            log.info("File scanner deletion job is disabled. Skipping...");
            return;
        }

        List<InstalledModEntity> modsToDeleteInDB = findModsToDeleteFromDB(installedModsInDB, installedFileSystemMods);
        modsToDeleteInDB.forEach(mod -> modService.deleteFromDB(mod.getId()).subscribeOn(Schedulers.boundedElastic()).subscribe());
    }

    private void installNewMods(List<InstalledModEntity> installedModsInDB, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        if (!aswgConfig.isFileScannerInstallationEnabled())
        {
            log.info("File scanner installation job is disabled. Skipping...");
            return;
        }

        List<InstalledModEntity> modsToAddToDB = findModsToAddToDB(installedModsInDB, installedFileSystemMods);
        modsToAddToDB.forEach(mod -> modService.saveToDB(mod).subscribeOn(Schedulers.boundedElastic()).subscribe());
    }

    private List<InstalledModEntity> findModsToAddToDB(List<InstalledModEntity> databaseMods, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        return installedFileSystemMods.stream()
                .filter(InstalledFileSystemMod::isValid)
                .filter(installedMod -> databaseMods.stream().noneMatch(databaseMod -> databaseMod.getWorkshopFileId() == installedMod.getWorkshopFileId()))
                .map(this::toEntity)
                .toList();
    }

    private InstalledModEntity toEntity(InstalledFileSystemMod installedFileSystemMod)
    {
        InstalledModEntity entity = new InstalledModEntity();
        entity.setWorkshopFileId(installedFileSystemMod.getWorkshopFileId());
        entity.setName(installedFileSystemMod.getName());
        entity.setDirectoryPath(installedFileSystemMod.getModDirectory().getPath().toString());
        entity.setCreatedDate(OffsetDateTime.now());

        ArmaWorkshopMod armaWorkshopMod = steamService.getWorkshopMod(installedFileSystemMod.getWorkshopFileId());
        if (armaWorkshopMod != null)
        {
            entity.setPreviewUrl(armaWorkshopMod.getPreviewUrl());
        }
        return entity;
    }

    private List<InstalledModEntity> findModsToDeleteFromDB(List<InstalledModEntity> databaseMods, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        return databaseMods.stream()
                .filter(installedDatabaseMod -> installedFileSystemMods.stream().noneMatch(fileSystemMod -> fileSystemMod.getWorkshopFileId() == installedDatabaseMod.getWorkshopFileId()))
                .toList();
    }
}
