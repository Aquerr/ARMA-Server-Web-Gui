package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.DiscordMessageCreator;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.MessageKind;

import java.util.Map;
import java.util.Optional;

public class DiscordIntegration
{
    private final ASWGConfig.DiscordProperties discordProperties;
    private final Map<MessageKind, DiscordMessageCreator> discordMessageCreators;
    private final DiscordWebhookHandler discordWebhookHandler;

    public DiscordIntegration(ASWGConfig.DiscordProperties discordProperties,
                              DiscordWebhookHandler discordWebhookHandler,
                              Map<MessageKind, DiscordMessageCreator> discordMessageCreators)
    {
        this.discordProperties = discordProperties;
        this.discordWebhookHandler = discordWebhookHandler;
        this.discordMessageCreators = discordMessageCreators;
    }

    public void sendMessage(MessageKind messageKind)
    {
        if (!discordProperties.isEnabled())
            return;

        Optional.ofNullable(this.discordMessageCreators.get(messageKind))
                .map(DiscordMessageCreator::create)
                .ifPresent(this.discordWebhookHandler::sendMessage);
    }
}
