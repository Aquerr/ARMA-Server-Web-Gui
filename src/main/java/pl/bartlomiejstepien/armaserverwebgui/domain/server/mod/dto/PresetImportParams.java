package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto;

import lombok.Value;

import java.util.List;

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
