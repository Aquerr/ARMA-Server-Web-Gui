package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.InstalledFileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;

import java.nio.file.Paths;

@Component
@AllArgsConstructor
public class InstalledModConverter
{
    private final ModWorkshopUrlBuilder workshopUrlBuilder;

    public ArmaWorkshopMod convertToWorkshopMod(InstalledModEntity installedModEntity)
    {
        if (installedModEntity == null)
            return null;

        return ArmaWorkshopMod.builder()
                .fileId(installedModEntity.getWorkshopFileId())
                .title(installedModEntity.getName())
                .previewUrl(installedModEntity.getPreviewUrl())
                .modWorkshopUrl(workshopUrlBuilder.buildUrlForFileId(installedModEntity.getWorkshopFileId()))
            .build();
    }

    public InstalledFileSystemMod toFileSystemMod(InstalledModEntity installedModEntity)
    {
        return InstalledFileSystemMod.from(Paths.get(installedModEntity.getDirectoryPath()));
    }
}
