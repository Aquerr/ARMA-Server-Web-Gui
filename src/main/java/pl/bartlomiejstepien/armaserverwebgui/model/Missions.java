package pl.bartlomiejstepien.armaserverwebgui.model;

import lombok.Data;

import java.util.List;

@Data
public class Missions
{
    private List<String> disabledMissions;
    private List<String> enabledMissions;
}