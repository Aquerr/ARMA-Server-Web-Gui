package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;

@Component
@AllArgsConstructor
public class InstalledModConverter
{
    private final ModWorkshopUrlBuilder workshopUrlBuilder;

    public ArmaWorkshopMod convertToWorkshopMod(InstalledMod installedMod)
    {
        if (installedMod == null)
            return null;

        return ArmaWorkshopMod.builder()
                .fileId(installedMod.getWorkshopFileId())
                .title(installedMod.getName())
                .previewUrl(installedMod.getPreviewUrl())
                .modWorkshopUrl(workshopUrlBuilder.buildUrlForFileId(installedMod.getWorkshopFileId()))
            .build();
    }
}
