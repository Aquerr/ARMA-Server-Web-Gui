package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;
import reactor.core.publisher.Mono;

public class DiscordWebhookHandler
{
    private final String webhookUrl;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public DiscordWebhookHandler(
            String webhookUrl,
            ObjectMapper objectMapper,
            WebClient webClient)
    {
        this.webhookUrl = webhookUrl;
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> sendMessage(DiscordMessage discordMessage)
    {
        String jsonMessage;
        try
        {
            jsonMessage = objectMapper.writeValueAsString(discordMessage);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }

        return webClient.post()
                .uri(this.webhookUrl)
                .body(BodyInserters.fromValue(jsonMessage))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
