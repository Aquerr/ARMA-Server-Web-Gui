package pl.bartlomiejstepien.armaserverwebgui.domain.discord.message;

import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface DiscordMessageCreator
{
    Mono<DiscordMessage> create();
}
