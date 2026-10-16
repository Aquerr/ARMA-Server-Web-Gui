package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModPreset
{
    private Long id;
    private String name;

    @Builder.Default
    private List<Entry> entries = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Entry
    {
        private Long id;
        private String name;
        private Long modId;
        private Long modPresetId;
    }
}
