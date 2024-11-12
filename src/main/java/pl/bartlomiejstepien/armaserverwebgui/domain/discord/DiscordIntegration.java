package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "discord.webhook.enabled")
public class DiscordIntegration
{

}

