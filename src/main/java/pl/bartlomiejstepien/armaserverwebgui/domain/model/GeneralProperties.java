package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GeneralProperties
{
    private String hostname;
    private int maxPlayers;
    private List<String> motd;
    private int motdInterval;
    private boolean persistent;
}
