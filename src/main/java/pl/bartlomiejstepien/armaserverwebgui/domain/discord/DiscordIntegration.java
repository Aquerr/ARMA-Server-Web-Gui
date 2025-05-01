package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import java.util.Map;
import java.util.Optional;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.DiscordMessageCreator;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.MessageKind;

public class DiscordIntegration
{
    private final Map<MessageKind, DiscordMessageCreator> discordMessageCreators;
    private final DiscordWebhookHandler discordWebhookHandler;

    public DiscordIntegration(DiscordWebhookHandler discordWebhookHandler,
                              Map<MessageKind, DiscordMessageCreator> discordMessageCreators)
    {
        this.discordWebhookHandler = discordWebhookHandler;
        this.discordMessageCreators = discordMessageCreators;
    }

    public void sendMessage(MessageKind messageKind)
    {
        Optional.ofNullable(this.discordMessageCreators.get(messageKind))
                .map(DiscordMessageCreator::create)
                .ifPresent(this.discordWebhookHandler::sendMessage);
    }
}
