package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.FileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class InstalledModEntityHelper
{
    private final SteamService steamService;

    public InstalledModEntity toEntity(FileSystemMod fileSystemMod)
    {
        InstalledModEntity.InstalledModEntityBuilder installedModBuilder = InstalledModEntity.builder();
        installedModBuilder.workshopFileId(fileSystemMod.getWorkshopFileId());
        installedModBuilder.name(fileSystemMod.getName());
        installedModBuilder.directoryPath(fileSystemMod.getModDirectory().getPath().toAbsolutePath().toString());
        installedModBuilder.createdDate(OffsetDateTime.now());
        installedModBuilder.lastWorkshopUpdate(fileSystemMod.getLastUpdated());

        tryPopulateModPreviewUrl(fileSystemMod.getWorkshopFileId(), installedModBuilder);
        return installedModBuilder.build();
    }

    private void tryPopulateModPreviewUrl(long publishedFileId,
                                          InstalledModEntity.InstalledModEntityBuilder installedModBuilder)
    {
        Optional.ofNullable(steamService.getWorkshopMod(publishedFileId))
                .ifPresent(workshopMod -> installedModBuilder.previewUrl(workshopMod.getPreviewUrl()));
    }
}
