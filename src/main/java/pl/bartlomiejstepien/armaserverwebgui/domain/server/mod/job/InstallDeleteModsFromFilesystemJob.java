package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.InstalledFileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@Component
@AllArgsConstructor
@Slf4j
public class InstallDeleteModsFromFilesystemJob
{
    private final ASWGConfig aswgConfig;
    private final ModService modService;
    private final SteamService steamService;

    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    public void scanModDirectories()
    {
        if (!this.aswgConfig.isFileScannerDeletionEnabled() && !this.aswgConfig.isFileScannerInstallationEnabled())
        {
            log.info("File scanner job is disabled. Skipping...");
            return;
        }

        log.info("Running mod directory scan.");

        List<InstalledFileSystemMod> installedFileSystemMods = modService.getInstalledModsFromFileSystem();
        saveOrDeleteModsFromDB(installedFileSystemMods);
    }

    private void saveOrDeleteModsFromDB(List<InstalledFileSystemMod> installedFileSystemMods)
    {
        modService.getInstalledMods()
                .collectList()
                .flatMapMany(installedModsInDB -> Flux.concat(deleteOldMods(installedModsInDB, installedFileSystemMods),
                        installNewMods(installedModsInDB, installedFileSystemMods)))
                .subscribe();
    }

    private Flux<Void> deleteOldMods(List<InstalledModEntity> installedModsInDB, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        if (!aswgConfig.isFileScannerDeletionEnabled())
        {
            log.info("File scanner deletion job is disabled. Skipping...");
            return Flux.empty();
        }

        List<InstalledModEntity> modsToDeleteInDB = findModsToDeleteFromDB(installedModsInDB, installedFileSystemMods);
        return Flux.fromIterable(modsToDeleteInDB)
                .filter(Objects::nonNull)
                .flatMapSequential(mod -> modService.deleteFromDB(mod.getId()));
    }

    private Flux<Void> installNewMods(List<InstalledModEntity> installedModsInDB, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        if (!aswgConfig.isFileScannerInstallationEnabled())
        {
            log.info("File scanner installation job is disabled. Skipping...");
            return Flux.empty();
        }

        List<InstalledModEntity> modsToAddToDB = findModsToAddToDB(installedModsInDB, installedFileSystemMods);
        return Flux.fromIterable(modsToAddToDB)
                .filter(Objects::nonNull)
                .flatMapSequential(this::saveToDB);
    }

    private Mono<Void> saveToDB(InstalledModEntity mod)
    {
        try
        {
            return modService.saveToDB(mod)
                    .doOnError(exception -> log.warn(format("Could not add mod to DB. Mod = %s", mod.toString()), exception))
                    .then();
        }
        catch (Exception exception)
        {
            return Mono.empty();
        }
    }

    private List<InstalledModEntity> findModsToAddToDB(List<InstalledModEntity> databaseMods, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        return installedFileSystemMods.stream()
                .filter(InstalledFileSystemMod::isValid)
                .filter(installedMod -> databaseMods.stream().noneMatch(databaseMod -> databaseMod.getWorkshopFileId() == installedMod.getWorkshopFileId()))
                .peek(mod -> log.info("Found new file system mod: {}", mod))
                .map(this::toEntity)
                .toList();
    }

    private InstalledModEntity toEntity(InstalledFileSystemMod installedFileSystemMod)
    {
        if (installedFileSystemMod.getWorkshopFileId() == 0)
        {
            log.warn("Installed mod {} has published file id = 0", installedFileSystemMod.getName());
            return null;
        }

        InstalledModEntity entity = new InstalledModEntity();
        entity.setWorkshopFileId(installedFileSystemMod.getWorkshopFileId());
        entity.setName(installedFileSystemMod.getName());
        entity.setDirectoryPath(installedFileSystemMod.getModDirectory().getPath().toString());
        entity.setCreatedDate(OffsetDateTime.now());

        try
        {
            ArmaWorkshopMod armaWorkshopMod = steamService.getWorkshopMod(installedFileSystemMod.getWorkshopFileId());
            if (armaWorkshopMod != null)
            {
                entity.setPreviewUrl(armaWorkshopMod.getPreviewUrl());
            }
        }
        catch (Exception exception)
        {
            // exception mostly ignored as it should not stop the process.
            log.warn(format("Could not fetch mod [%s] preview url.", installedFileSystemMod.getWorkshopFileId()), exception);
        }
        return entity;
    }

    private List<InstalledModEntity> findModsToDeleteFromDB(List<InstalledModEntity> databaseMods, List<InstalledFileSystemMod> installedFileSystemMods)
    {
        return databaseMods.stream()
                .filter(installedDatabaseMod -> installedFileSystemMods.stream().noneMatch(fileSystemMod -> fileSystemMod.getWorkshopFileId() == installedDatabaseMod.getWorkshopFileId()))
                .peek(mod -> log.info("Found mod to delete: " + mod))
                .toList();
    }
}
