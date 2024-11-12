package pl.bartlomiejstepien.armaserverwebgui.domain.discord.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordWebhookMessage
{
    private String content;
}
