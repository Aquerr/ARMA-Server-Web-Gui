package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ArmaWorkshopMod
{
    private Integer fileId;
    private String title;
    private String description;
    private String previewUrl;
}
