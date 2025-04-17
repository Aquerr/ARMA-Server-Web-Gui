package pl.bartlomiejstepien.armaserverwebgui.domain.server.general.model;

import lombok.Builder;
import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;

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
    private boolean drawingInMap;
    private List<String> headlessClients;
    private List<String> localClients;
    private Mission.Difficulty forcedDifficulty;
}
