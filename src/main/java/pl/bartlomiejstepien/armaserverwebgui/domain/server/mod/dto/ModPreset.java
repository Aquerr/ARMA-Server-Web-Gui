package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModPreset
{
    private Long id;
    private String name;

    @Builder.Default
    private List<Entry> entries = new ArrayList<>();

    @Data
    @Builder(toBuilder = true)
    public static class Entry
    {
        private Long id;
        private String name;
        private Long modId;
        private Long modPresetId;
    }
}
