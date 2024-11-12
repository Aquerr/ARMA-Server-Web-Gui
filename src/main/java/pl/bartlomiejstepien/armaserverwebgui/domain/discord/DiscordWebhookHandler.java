package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordWebhookMessageParams;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

class DiscordWebhookHandler
{
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    DiscordWebhookHandler(ObjectMapper objectMapper,
                              WebClient webClient)
    {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> sendMessage(String discordWebhookUrl,
                                  DiscordWebhookMessageParams params)
    {
        String jsonMessage;
        try
        {
            DiscordMessage discordMessage = new DiscordMessage(List.of(
                    DiscordMessage.Embed.builder()
                            .title(params.getTitle())
                            .description(params.getDescription())
                            .fields(Optional.ofNullable(params.getFields())
                                    .map(map -> map.entrySet().stream().map(entry -> DiscordMessage.Embed.Field.builder()
                                                    .name(entry.getKey())
                                                    .value(entry.getValue())
                                                    .build())
                                            .toList())
                                    .orElse(null))
                            .build()
            ));

            jsonMessage = objectMapper.writeValueAsString(discordMessage);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }

        return webClient.post()
                .uri(discordWebhookUrl)
                .body(BodyInserters.fromValue(jsonMessage))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
