package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class ModPresetSaveParams
{
    String name;
    List<String> modNames;
}
