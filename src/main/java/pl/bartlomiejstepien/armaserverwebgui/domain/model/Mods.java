package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import lombok.Data;

import java.util.Set;

@Data
public class Mods
{
    private Set<Mod> disabledMods;
    private Set<Mod> enabledMods;
}
