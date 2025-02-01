package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.DiscordMessageCreator;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.MessageKind;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DiscordIntegrationTest
{
    @Mock
    private DiscordWebhookHandler discordWebhookHandler;

    @Mock
    private DiscordMessageCreator messageCreator;

    @InjectMocks
    private DiscordIntegration discordIntegration;

    @BeforeEach
    void setUp()
    {
        Map<MessageKind, DiscordMessageCreator> messageCreators = Map.of(
                MessageKind.SERVER_STARTED, messageCreator
        );
        discordIntegration = new DiscordIntegration(discordWebhookHandler, messageCreators);
    }

    @Test
    void shouldSendMessage()
    {
        // given
        DiscordMessage discordMessage = DiscordMessage.ofSingleEmbed(DiscordMessage.Embed.builder()
                        .title("Test")
                .build());

        given(messageCreator.create()).willReturn(discordMessage);

        // when
        discordIntegration.sendMessage(MessageKind.SERVER_STARTED);

        // then
        verify(discordWebhookHandler).sendMessage(discordMessage);
    }
}