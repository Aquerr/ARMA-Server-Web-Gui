package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mission
{
    private String name;
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
