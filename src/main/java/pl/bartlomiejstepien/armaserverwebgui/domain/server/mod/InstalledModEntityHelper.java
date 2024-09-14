package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.InstalledFileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.MetaCppFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;

import java.time.OffsetDateTime;

@Slf4j
@AllArgsConstructor
@Component
public class InstalledModEntityHelper
{
    private final SteamService steamService;

    public InstalledModEntity toEntity(InstalledFileSystemMod fileSystemMod)
    {
        MetaCppFile metaCppFile = fileSystemMod.getModMetaFile().orElseThrow();
        InstalledModEntity.InstalledModEntityBuilder installedModBuilder = InstalledModEntity.builder();
        installedModBuilder.workshopFileId(metaCppFile.getPublishedFileId());
        installedModBuilder.name(metaCppFile.getName());
        installedModBuilder.directoryPath(fileSystemMod.getModDirectory().getPath().toAbsolutePath().toString());
        installedModBuilder.createdDate(OffsetDateTime.now());

        tryPopulateModPreviewUrl(metaCppFile.getPublishedFileId(), metaCppFile.getName(), installedModBuilder);
        return installedModBuilder.build();
    }

    private void tryPopulateModPreviewUrl(long publishedFileId,
                                          String modName,
                                          InstalledModEntity.InstalledModEntityBuilder installedModBuilder)
    {
        try
        {
            ArmaWorkshopMod armaWorkshopMod = steamService.getWorkshopMod(publishedFileId);
            if (armaWorkshopMod != null)
            {
                installedModBuilder.previewUrl(armaWorkshopMod.getPreviewUrl());
            }
        }
        catch (Exception exception)
        {
            log.warn("Could not fetch mod preview url. Mod = {}", modName, exception);
        }
    }
}
