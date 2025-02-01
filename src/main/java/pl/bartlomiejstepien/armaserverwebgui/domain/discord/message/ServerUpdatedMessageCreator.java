package pl.bartlomiejstepien.armaserverwebgui.domain.discord.message;

import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;

public class ServerUpdatedMessageCreator implements DiscordMessageCreator
{

    @Override
    public DiscordMessage create()
    {
        return DiscordMessage.ofSingleEmbed(DiscordMessage.Embed.builder()
                .title("Updating server")
                .build());
    }
}
