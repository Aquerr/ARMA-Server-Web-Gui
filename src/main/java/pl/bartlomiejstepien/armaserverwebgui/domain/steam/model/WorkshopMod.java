package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

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
    private List<Long> dependencies;
}
