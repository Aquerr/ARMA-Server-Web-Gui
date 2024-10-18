package pl.bartlomiejstepien.armaserverwebgui.domain.discord.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class DiscordWebhookMessageParams
{
    String title;
    String description;
    Map<String, String> fields;
}
