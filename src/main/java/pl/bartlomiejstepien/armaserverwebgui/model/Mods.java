package pl.bartlomiejstepien.armaserverwebgui.model;

import lombok.Data;

import java.util.Set;

@Data
public class Mods
{
    private Set<String> disabledMods;
    private Set<String> enabledMods;
}
