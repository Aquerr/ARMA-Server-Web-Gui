package pl.bartlomiejstepien.armaserverwebgui.domain.discord.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordMessage
{
    private List<Embed> embeds;

    @Data
    @Builder
    public static class Embed
    {
        private String title;
        private String description;
        private int color;
        private List<Field> fields;
        private final String type = "rich";

        @Data
        @Builder
        public static class Field
        {
            private String name;
            private String value;
            private boolean inline;
        }
    }

    public static DiscordMessage ofSingleEmbed(Embed embed)
    {
        return new DiscordMessage(List.of(embed));
    }
}
