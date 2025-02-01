package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.InstalledModEntityHelper;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.InstalledFileSystemMod;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@Slf4j
public class InstallDeleteModsFromFilesystemJob
{
    private final ASWGConfig aswgConfig;
    private final ModService modService;
    private final InstalledModEntityHelper helper;

    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    public void scanModDirectories()
    {
        if (!this.aswgConfig.isModsScannerDeletionEnabled() && !this.aswgConfig.isModsScannerInstallationEnabled())
        {
            log.info("Mods file scanner job is disabled. Skipping...");
            return;
        }

        log.info("Running mod directory scan.");

        List<InstalledFileSystemMod> installedFileSystemMods = modService.getInstalledModsFromFileSystem();
        saveOrDeleteModsFromDB(installedFileSystemMods);
    }

    private void saveOrDeleteModsFromDB(List<InstalledFileSystemMod> installedFileSystemMods)
    {
        List<InstalledModEntity> installedModsInDB = modService.getInstalledMods();
        deleteOldMods(installedModsInDB, installedFileSystemMods);
        installNewMods(installedModsInDB, installedFileSystemMods);
    }

    private void deleteOldMods(List<InstalledModEntity> installedModsInDB, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        if (!aswgConfig.isModsScannerDeletionEnabled())
        {
            log.info("File scanner deletion job is disabled. Skipping...");
            return;
        }

        List<InstalledModEntity> modsToDeleteInDB = findModsToDeleteFromDB(installedModsInDB, installedFileSystemMods);
        log.info("Mods to delete: {}", Arrays.toString(modsToDeleteInDB.toArray()));
        modsToDeleteInDB.stream()
                .map(InstalledModEntity::getId)
                .forEach(modService::deleteFromDB);
    }

    private void installNewMods(List<InstalledModEntity> installedModsInDB, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        if (!aswgConfig.isModsScannerInstallationEnabled())
        {
            log.info("File scanner installation job is disabled. Skipping...");
            return;
        }

        List<InstalledModEntity> modsToAddToDB = findModsToAddToDB(installedModsInDB, installedFileSystemMods);
        log.info("Mods to add: {}", Arrays.toString(modsToAddToDB.toArray()));
        modsToAddToDB.forEach(this::saveToDB);
    }

    private void saveToDB(InstalledModEntity mod)
    {
        try
        {
            modService.saveToDB(mod);
        }
        catch (Exception exception)
        {
            log.warn("Could not add mod to DB. Mod = {}", mod.toString(), exception);
        }
    }

    private List<InstalledModEntity> findModsToAddToDB(List<InstalledModEntity> databaseMods, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        return installedFileSystemMods.stream()
                .filter(InstalledFileSystemMod::isValid)
                .filter(installedMod -> databaseMods.stream().noneMatch(databaseMod -> databaseMod.getWorkshopFileId() == installedMod.getWorkshopFileId()))
                .map(this::toEntity)
                .filter(Objects::nonNull)
                .toList();
    }

    private InstalledModEntity toEntity(InstalledFileSystemMod installedFileSystemMod)
    {
        if (installedFileSystemMod.getWorkshopFileId() == 0)
        {
            log.warn("Installed mod {} has published file id = 0", installedFileSystemMod.getName());
            return null;
        }

        return helper.toEntity(installedFileSystemMod);
    }

    private List<InstalledModEntity> findModsToDeleteFromDB(List<InstalledModEntity> databaseMods, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        return databaseMods.stream()
                .filter(installedDatabaseMod -> installedFileSystemMods.stream().noneMatch(fileSystemMod -> fileSystemMod.getWorkshopFileId() == installedDatabaseMod.getWorkshopFileId()))
                .toList();
    }
}
