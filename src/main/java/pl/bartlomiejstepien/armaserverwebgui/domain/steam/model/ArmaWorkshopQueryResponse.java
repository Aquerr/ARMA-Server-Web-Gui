package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ArmaWorkshopQueryResponse
{
    private String nextCursor;
    private List<ArmaWorkshopMod> mods;
}
