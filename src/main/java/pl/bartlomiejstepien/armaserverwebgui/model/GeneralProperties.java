package pl.bartlomiejstepien.armaserverwebgui.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GeneralProperties
{
    private int maxPlayers;
    private List<String> motd;
    private boolean persistent;
}
