package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;

import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

public class DiscordWebhookHandler
{
    private final String webhookUrl;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public DiscordWebhookHandler(
            String webhookUrl,
            ObjectMapper objectMapper,
            RestClient restClient)
    {
        this.webhookUrl = webhookUrl;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(DiscordMessage discordMessage)
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

        restClient.post()
                .uri(this.webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonMessage)
                .retrieve()
                .toBodilessEntity();
    }
}
