package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ModPreset
{
    private Long id;
    private String name;
    private List<Entry> entries;

    @Data
    @Builder
    public static class Entry
    {
        private Long id;
        private String name;
        private Long modId;
        private Long modPresetId;
    }
}
