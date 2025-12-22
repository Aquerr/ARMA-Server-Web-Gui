package pl.bartlomiejstepien.armaserverwebgui.domain.discord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.model.DiscordMessage;
import tools.jackson.databind.ObjectMapper;

@Slf4j
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
        if (!StringUtils.hasText(this.webhookUrl))
        {
            log.warn("Discord webhook url is not set. Cannot send message to Discord.");
            return;
        }

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
