package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.DiscordMessageCreator;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.MessageKind;
import reactor.core.publisher.Mono;

import java.util.Map;

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

    public Mono<Void> sendMessage(MessageKind messageKind)
    {
        return this.discordMessageCreators.get(messageKind).create()
                .flatMap(this.discordWebhookHandler::sendMessage);
    }
}
