package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModDirectory;

import java.nio.file.Path;

@Data
@Builder
public class ModData
{
    private long fileId;
    private String title;
    private ModDirectory modDirectory;
    private InstalledModEntity installedModEntity;
    private WorkshopMod workshopMod;
    private Path steamCmdModFolderPath;
}