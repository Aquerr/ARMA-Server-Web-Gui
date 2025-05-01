package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;

@Component
@AllArgsConstructor
public class InstalledModConverter
{
    private final ModWorkshopUrlBuilder workshopUrlBuilder;

    public WorkshopMod convertToWorkshopMod(InstalledModEntity installedModEntity)
    {
        if (installedModEntity == null)
            return null;

        return WorkshopMod.builder()
                .fileId(installedModEntity.getWorkshopFileId())
                .title(installedModEntity.getName())
                .previewUrl(installedModEntity.getPreviewUrl())
                .modWorkshopUrl(workshopUrlBuilder.buildUrlForFileId(installedModEntity.getWorkshopFileId()))
                .build();
    }
}
