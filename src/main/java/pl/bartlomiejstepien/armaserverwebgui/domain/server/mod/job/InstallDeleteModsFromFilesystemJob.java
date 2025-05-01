package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.InstalledModEntityHelper;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.FileSystemMod;

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

        List<FileSystemMod> fileSystemMods = modService.getInstalledModsFromFileSystem();
        saveOrDeleteModsFromDB(fileSystemMods);
    }

    private void saveOrDeleteModsFromDB(List<FileSystemMod> fileSystemMods)
    {
        List<InstalledModEntity> installedModsInDB = modService.getInstalledMods();
        List<FileSystemMod> notManagedMods = modService.findNotManagedMods();
        deleteModsWithoutFiles(installedModsInDB, fileSystemMods);
        installNewMods(notManagedMods);
    }

    private void deleteModsWithoutFiles(List<InstalledModEntity> installedModsInDB, List<FileSystemMod> fileSystemMods)
    {
        if (!aswgConfig.isModsScannerDeletionEnabled())
        {
            log.info("File scanner deletion job is disabled. Skipping...");
            return;
        }

        List<InstalledModEntity> modsToDeleteInDB = findModsToDeleteFromDB(installedModsInDB, fileSystemMods);
        log.info("Mods to delete: {}", Arrays.toString(modsToDeleteInDB.toArray()));
        modsToDeleteInDB.stream()
                .map(InstalledModEntity::getId)
                .forEach(modService::deleteFromDB);
    }

    private void installNewMods(List<FileSystemMod> fileSystemMods)
    {
        if (!aswgConfig.isModsScannerInstallationEnabled())
        {
            log.info("File scanner installation job is disabled. Skipping...");
            return;
        }

        log.info("Mods to add: {}", Arrays.toString(fileSystemMods.toArray()));
        fileSystemMods.forEach(this::manageMod);
    }

    private void manageMod(FileSystemMod mod)
    {
        try
        {
            modService.manageMod(mod.getName());
        }
        catch (Exception exception)
        {
            log.warn("Could not add mod to DB. Mod = {}", mod.toString(), exception);
        }
    }

    private List<InstalledModEntity> findModsToAddToDB(List<InstalledModEntity> databaseMods, List<FileSystemMod> fileSystemMods)
    {
        return fileSystemMods.stream()
                .filter(FileSystemMod::isValid)
                .filter(installedMod -> databaseMods.stream().noneMatch(databaseMod -> databaseMod.getWorkshopFileId() == installedMod.getWorkshopFileId()))
                .map(this::toEntity)
                .filter(Objects::nonNull)
                .toList();
    }

    private InstalledModEntity toEntity(FileSystemMod fileSystemMod)
    {
        if (fileSystemMod.getWorkshopFileId() == 0)
        {
            log.warn("Installed mod {} has published file id = 0", fileSystemMod.getName());
            return null;
        }

        return helper.toEntity(fileSystemMod);
    }

    private List<InstalledModEntity> findModsToDeleteFromDB(List<InstalledModEntity> databaseMods, List<FileSystemMod> fileSystemMods)
    {
        return databaseMods.stream()
                .filter(installedDatabaseMod -> fileSystemMods.stream()
                        .noneMatch(fileSystemMod -> fileSystemMod.getWorkshopFileId() == installedDatabaseMod.getWorkshopFileId()))
                .toList();
    }
}
