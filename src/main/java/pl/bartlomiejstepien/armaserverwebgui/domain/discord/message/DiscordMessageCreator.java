package pl.bartlomiejstepien.armaserverwebgui.domain.discord.message;

import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;

@FunctionalInterface
public interface DiscordMessageCreator
{
    DiscordMessage create();
}
