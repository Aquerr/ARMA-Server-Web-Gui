package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Builder
@Data
public class WorkshopMod
{
    private Long fileId;
    private String title;
    private String description;
    private String previewUrl;
    private String modWorkshopUrl;
    private OffsetDateTime lastUpdate;
}
