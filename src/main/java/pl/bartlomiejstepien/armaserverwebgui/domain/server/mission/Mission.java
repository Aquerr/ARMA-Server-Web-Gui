package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mission
{
    private String name;
    private Difficulty difficulty;
    private Set<Parameter> parameters = new HashSet<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Parameter
    {
        private String name;
        private String value;
    }

    public enum Difficulty
    {
        RECRUIT,
        REGULAR,
        VETERAN,
        CUSTOM;

        public static Difficulty findOrDefault(String value)
        {
            return Stream.of(values())
                    .filter(difficulty -> difficulty.name().toUpperCase().equals(value))
                    .findFirst()
                    .orElse(REGULAR);
        }
    }
}
