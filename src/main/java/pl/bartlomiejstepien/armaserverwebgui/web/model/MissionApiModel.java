package pl.bartlomiejstepien.armaserverwebgui.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionApiModel
{
    private Long id;
    private String name;
    private String template;
    private Long sizeBytes;

    private String difficulty;

    private boolean enabled;

    private Set<Parameter> parameters = new HashSet<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Parameter
    {
        private String name;
        private String value;
    }
}
