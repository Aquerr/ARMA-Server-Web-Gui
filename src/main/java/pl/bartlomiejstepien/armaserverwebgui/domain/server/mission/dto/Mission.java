package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mission
{
    private Long id;
    private String name;
    private String template;

    @Builder.Default
    private Difficulty difficulty = Difficulty.REGULAR;

    @Builder.Default
    private boolean enabled = false;

    @Builder.Default
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
            return Optional.ofNullable(value)
                    .map(String::toUpperCase)
                    .flatMap(searchedValue -> Stream.of(values()).filter(difficulty -> difficulty.name().equals(searchedValue))
                            .findFirst())
                    .orElse(REGULAR);
        }
    }
}
