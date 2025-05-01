package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto;

import java.util.List;
import lombok.Data;

@Data
public class Missions
{
    private List<Mission> disabledMissions;
    private List<Mission> enabledMissions;
}
