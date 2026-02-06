package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import lombok.Value;

import java.util.List;

@Value
public class ModDependencies
{
    List<RelatedMod> mods;
}
