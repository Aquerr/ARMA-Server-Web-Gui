package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WorkshopQueryParams
{
    String cursor;
    String searchText;
}
