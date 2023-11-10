package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
public class InstallDeleteModsFromFilesystemJob
{
    private final ModService modService;
    private final SteamService steamService;

    @Scheduled(fixedDelay = 1L, timeUnit = TimeUnit.HOURS)
    public void scanModDirectories()
    {
        List<InstalledFileSystemMod> installedFileSystemMods = modService.getInstalledModsFromFileSystem();
        saveOrDeleteModsFromDB(installedFileSystemMods);
    }

    private void saveOrDeleteModsFromDB(List<InstalledFileSystemMod> installedFileSystemMods)
    {
        modService.getInstalledMods()
                .collectList()
                .map(installedModsInDB -> {
                    List<InstalledModEntity> modsToAddToDB = findModsToAddToDB(installedModsInDB, installedFileSystemMods);
                    List<InstalledModEntity> modsToDeleteInDB = findModsToDeleteFromDB(installedModsInDB, installedFileSystemMods);

                    modsToAddToDB.forEach(mod -> modService.saveToDB(mod).subscribeOn(Schedulers.boundedElastic()).subscribe());
                    modsToDeleteInDB.forEach(mod -> modService.deleteFromDB(mod.getId()).subscribeOn(Schedulers.boundedElastic()).subscribe());
                    return installedModsInDB;
                }).subscribe();
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
