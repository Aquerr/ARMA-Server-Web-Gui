package pl.bartlomiejstepien.armaserverwebgui.domain.discord.message;

import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;
import reactor.core.publisher.Mono;

public class ServerStartedMessageCreator implements DiscordMessageCreator
{
    @Override
    public Mono<DiscordMessage> create()
    {
        return Mono.just(DiscordMessage.ofSingleEmbed(DiscordMessage.Embed.builder()
                .title("Server started")
                .build()));
    }
}
