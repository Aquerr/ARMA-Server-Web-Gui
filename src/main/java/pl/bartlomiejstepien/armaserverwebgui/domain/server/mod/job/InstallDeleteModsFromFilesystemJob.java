package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import reactor.core.Disposable;
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
    public Disposable scanModDirectories()
    {
        List<InstalledMod> installedModsInFileSystem = modService.getInstalledModsFromFileSystem();
        return saveOrDeleteModsFromDB(installedModsInFileSystem);
    }

    private Disposable saveOrDeleteModsFromDB(List<InstalledMod> installedModsInFileSystem)
    {
        return modService.getInstalledMods()
                .collectList()
                .subscribeOn(Schedulers.single())
                .doOnSuccess(installedModsInDB -> {
                    List<InstalledMod> modsToAddToDB = findModsToAddToDB(installedModsInDB, installedModsInFileSystem);
                    List<InstalledMod> modsToDeleteInDB = findModsToDeleteFromDB(installedModsInDB, installedModsInFileSystem);

                    modsToAddToDB.forEach(mod -> modService.saveToDB(mod).subscribe());
                    modsToDeleteInDB.forEach(mod -> modService.deleteFromDB(mod.getId()).subscribe());
                }).subscribe();
    }

    private List<InstalledMod> findModsToAddToDB(List<InstalledMod> databaseMods, List<InstalledMod> installedFileSystemMods)
    {
        return installedFileSystemMods.stream()
                .filter(installedMod -> databaseMods.stream().noneMatch(databaseMod -> databaseMod.getPublishedFileId() == installedMod.getPublishedFileId()))
                .map(this::populateMod)
                .toList();
    }

    private InstalledMod populateMod(InstalledMod installedMod)
    {
        ArmaWorkshopMod armaWorkshopMod = steamService.getWorkshopMod(installedMod.getPublishedFileId());
        if (armaWorkshopMod != null)
        {
            installedMod.setPreviewUrl(armaWorkshopMod.getPreviewUrl());
        }

        installedMod.setCreatedDate(OffsetDateTime.now());
        return installedMod;
    }

    private List<InstalledMod> findModsToDeleteFromDB(List<InstalledMod> databaseMods, List<InstalledMod> installedFileSystemMods)
    {
        return databaseMods.stream()
                .filter(installedDatabaseMod -> installedFileSystemMods.stream().noneMatch(fileSystemMod -> fileSystemMod.getPublishedFileId() == installedDatabaseMod.getPublishedFileId()))
                .toList();
    }
}
