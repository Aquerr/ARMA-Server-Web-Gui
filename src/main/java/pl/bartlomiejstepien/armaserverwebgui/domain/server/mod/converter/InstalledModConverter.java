package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.InstalledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;

@Component
public class InstalledModConverter
{
    public ArmaWorkshopMod convertToWorkshopMod(InstalledMod installedMod)
    {
        if (installedMod == null)
            return null;

        return ArmaWorkshopMod.builder()
                .fileId(installedMod.getPublishedFileId())
                .title(installedMod.getName())
                .previewUrl(installedMod.getPreviewUrl())
                .modWorkshopUrl("https://steamcommunity.com/sharedfiles/filedetails/?id=" + installedMod.getPublishedFileId())
            .build();
    }
}
