package pl.bartlomiejstepien.armaserverwebgui.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeneralProperties
{
    private int maxPlayers;
}
