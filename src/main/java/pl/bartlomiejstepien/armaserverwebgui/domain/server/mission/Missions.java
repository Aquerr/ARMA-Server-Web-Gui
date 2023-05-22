package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import lombok.Data;

import java.util.List;

@Data
public class Missions
{
    private List<Mission> disabledMissions;
    private List<Mission> enabledMissions;
}
