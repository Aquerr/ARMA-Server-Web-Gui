package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ArmaWorkshopQueryResponse
{
    private String nextCursor;
    private List<WorkshopMod> mods;
}
