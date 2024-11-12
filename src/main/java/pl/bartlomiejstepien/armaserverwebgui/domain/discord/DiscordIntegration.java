package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordWebhookMessageParams;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(value = "aswg.discord.webhook.enabled")
public class DiscordIntegration
{
    private final ASWGConfig config;
    private final DiscordWebhookHandler discordWebhookHandler;

    public DiscordIntegration(ASWGConfig config,
                              ObjectMapper objectMapper,
                              WebClient.Builder webClientBuilder)
    {
        this.config = config;
        WebClient webClient = webClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.discordWebhookHandler = new DiscordWebhookHandler(objectMapper, webClient);
    }

    public Mono<Void> sendMessage(DiscordWebhookMessageParams params)
    {
        return this.discordWebhookHandler.sendMessage(this.config.getDiscordWebhookUrl(), params);
    }
}
