package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DiscordWebhookHandlerTest
{
    private static final String webhookUrl = "test";
    @Mock
    private RestClient restClient;
    @Mock
    private ObjectMapper objectMapper;

    private DiscordWebhookHandler discordWebhookHandler;

    @BeforeEach
    void setUp()
    {
        this.discordWebhookHandler = new DiscordWebhookHandler(webhookUrl, objectMapper, restClient);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenCannotConvertMessageToJson() throws JsonProcessingException
    {
        // given
        DiscordMessage discordMessage = DiscordMessage.ofSingleEmbed(DiscordMessage.Embed.builder()
            .title("test")
            .build());

        given(objectMapper.writeValueAsString(discordMessage)).willThrow(JsonProcessingException.class);

        // when
        Throwable throwable = catchException(() -> discordWebhookHandler.sendMessage(discordMessage));

        // then
        assertThat(throwable).isInstanceOf(RuntimeException.class);
    }
}