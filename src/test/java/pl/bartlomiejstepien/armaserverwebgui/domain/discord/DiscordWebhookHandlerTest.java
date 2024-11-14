package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DiscordWebhookHandlerTest
{
    private static final String webhookUrl = "test";
    @Mock
    private WebClient webClient;
    @Mock
    private ObjectMapper objectMapper;

    private DiscordWebhookHandler discordWebhookHandler;

    @BeforeEach
    void setUp()
    {
        this.discordWebhookHandler = new DiscordWebhookHandler(webhookUrl, objectMapper, webClient);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenCannotConvertMessageToJson() throws JsonProcessingException
    {
        DiscordMessage discordMessage = DiscordMessage.ofSingleEmbed(DiscordMessage.Embed.builder()
            .title("test")
            .build());

        given(objectMapper.writeValueAsString(discordMessage)).willThrow(JsonProcessingException.class);

        // then
        StepVerifier.create(Mono.defer(() -> discordWebhookHandler.sendMessage(discordMessage)))
                .verifyErrorMatches(RuntimeException.class::isInstance);
    }
}