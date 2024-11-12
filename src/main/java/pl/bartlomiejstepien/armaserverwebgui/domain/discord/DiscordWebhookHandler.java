package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordWebhookMessage;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordWebhookMessageParams;
import reactor.core.publisher.Mono;

@Component
public class DiscordWebhookHandler
{
    private final ASWGConfig config;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public DiscordWebhookHandler(ASWGConfig config,
                                 ObjectMapper objectMapper,
                                 WebClient.Builder webClientBuilder)
    {
        this.config = config;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<Void> publishMessage(DiscordWebhookMessageParams params)
    {
        String jsonMessage;
        try
        {
            jsonMessage = objectMapper.writeValueAsString(new DiscordWebhookMessage(params.getTitle()));
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }

        return webClient.post()
                .uri(config.getDiscordWebhookUrl())
                .body(BodyInserters.fromValue(jsonMessage))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
