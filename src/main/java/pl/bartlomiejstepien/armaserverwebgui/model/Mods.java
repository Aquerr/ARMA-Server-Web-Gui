package pl.bartlomiejstepien.armaserverwebgui.model;

import lombok.Data;

import java.util.List;

@Data
public class Mods
{
    private List<String> disabledMods;
    private List<String> enabledMods;
}
