package pl.bartlomiejstepien.armaserverwebgui.domain.discord.message;

import lombok.RequiredArgsConstructor;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;

@RequiredArgsConstructor
public class PlayerJoinedMessageCreator implements DiscordMessageCreator
{
    private final ASWGConfig aswgConfig;

    @Override
    public DiscordMessage create()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
