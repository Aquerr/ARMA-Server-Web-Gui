package pl.bartlomiejstepien.armaserverwebgui.domain.discord.message;

import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;
import reactor.core.publisher.Mono;

public class PlayerJoinedMessageCreator implements DiscordMessageCreator
{

    @Override
    public Mono<DiscordMessage> create()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
