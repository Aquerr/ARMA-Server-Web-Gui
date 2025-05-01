package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import java.util.List;
import lombok.Value;

@Value(staticConstructor = "of")
public class ModPresetSaveParams
{
    String name;
    List<String> modNames;
}
