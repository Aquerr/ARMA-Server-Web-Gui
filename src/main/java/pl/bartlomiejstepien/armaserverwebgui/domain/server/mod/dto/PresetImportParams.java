package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto;

import java.util.List;
import lombok.Value;

@Value(staticConstructor = "of")
public class PresetImportParams
{
    String name;
    List<ModParam> modParams;

    @Value(staticConstructor = "of")
    public static class ModParam
    {
        String title;
        long id;
    }
}
