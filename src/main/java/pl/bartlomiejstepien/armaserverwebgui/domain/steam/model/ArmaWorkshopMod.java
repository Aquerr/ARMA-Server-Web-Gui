package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ArmaWorkshopMod
{
    private Long fileId;
    private String title;
    private String description;
    private String previewUrl;
}
