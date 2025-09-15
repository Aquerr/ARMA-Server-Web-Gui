package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.AswgJob;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.JobExecutionInfoService;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.AswgJobNames;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.FileSystemMod;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class InstallDeleteModsFromFilesystemJob extends AswgJob
{
    public static final String DELETION_ENABLED_PROPERTY = "DELETION_ENABLED";
    public static final String INSTALLATION_ENABLED_PROPERTY = "INSTALLATION_ENABLED";

    private final ASWGConfig aswgConfig;
    private final ModService modService;

    public InstallDeleteModsFromFilesystemJob(JobExecutionInfoService jobExecutionInfoService,
                                              ASWGConfig aswgConfig,
                                              ModService modService)
    {
        super(jobExecutionInfoService);
        this.aswgConfig = aswgConfig;
        this.modService = modService;
    }

    @Override
    public void runJob()
    {
        if (!this.aswgConfig.getJobsProperties().isModsScannerDeletionEnabled()
                && !this.aswgConfig.getJobsProperties().isModsScannerInstallationEnabled())
        {
            log.info("Mods file scanner job is disabled. Skipping...");
            return;
        }

        log.info("Running mod directory scan.");
        saveOrDeleteModsFromDB();
    }

    private void saveOrDeleteModsFromDB()
    {
        List<FileSystemMod> fileSystemMods = modService.getInstalledModsFromFileSystem();
        List<InstalledModEntity> installedModsInDB = modService.getInstalledMods();
        List<FileSystemMod> notManagedMods = modService.findNotManagedMods();
        deleteModsWithoutFiles(installedModsInDB, fileSystemMods);
        installNewMods(notManagedMods);
    }

    private void deleteModsWithoutFiles(List<InstalledModEntity> installedModsInDB, List<FileSystemMod> fileSystemMods)
    {
        if (!aswgConfig.getJobsProperties().isModsScannerDeletionEnabled())
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
        if (!aswgConfig.getJobsProperties().isModsScannerInstallationEnabled())
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

    private List<InstalledModEntity> findModsToDeleteFromDB(List<InstalledModEntity> databaseMods, List<FileSystemMod> fileSystemMods)
    {
        return databaseMods.stream()
                .filter(installedDatabaseMod -> fileSystemMods.stream()
                        .noneMatch(fileSystemMod -> fileSystemMod.getWorkshopFileId() == installedDatabaseMod.getWorkshopFileId()))
                .toList();
    }

    @Override
    public String getName()
    {
        return AswgJobNames.INSTALL_DELETE_MODS;
    }
}
