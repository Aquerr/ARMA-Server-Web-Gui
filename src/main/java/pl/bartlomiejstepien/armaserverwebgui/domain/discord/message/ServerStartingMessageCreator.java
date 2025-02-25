package pl.bartlomiejstepien.armaserverwebgui.domain.discord.message;

import lombok.RequiredArgsConstructor;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;

@RequiredArgsConstructor
public class ServerStartingMessageCreator implements DiscordMessageCreator
{
    private final ASWGConfig aswgConfig;

    @Override
    public DiscordMessage create()
    {
        return DiscordMessage.ofSingleEmbed(DiscordMessage.Embed.builder()
                .title(aswgConfig.getDiscordProperties().getMessageServerStarting())
                .build());
    }
}
