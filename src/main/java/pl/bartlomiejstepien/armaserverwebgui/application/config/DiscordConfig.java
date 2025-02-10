package pl.bartlomiejstepien.armaserverwebgui.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.DiscordIntegration;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.DiscordWebhookHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.DiscordMessageCreator;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.MessageKind;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.PlayerJoinedMessageCreator;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.ServerStartedMessageCreator;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.ServerStartingMessageCreator;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.ServerStoppedMessageCreator;
import pl.bartlomiejstepien.armaserverwebgui.domain.discord.message.ServerUpdatedMessageCreator;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty("aswg.discord.webhook.enabled")
public class DiscordConfig
{
    @Bean
    public RestClient discordRestClient(RestClient.Builder builder,
                                        Logbook logbook)
    {
        return builder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(new LogbookClientHttpRequestInterceptor(logbook))
                .build();
    }

    @Bean
    public PlayerJoinedMessageCreator playerJoinedMessageCreator()
    {
        return new PlayerJoinedMessageCreator();
    }

    @Bean
    public ServerStartingMessageCreator serverStartingMessageCreator()
    {
        return new ServerStartingMessageCreator();
    }

    @Bean
    public ServerStartedMessageCreator serverStartedMessageCreator()
    {
        return new ServerStartedMessageCreator();
    }

    @Bean
    public ServerUpdatedMessageCreator serverUpdatedMessageCreator()
    {
        return new ServerUpdatedMessageCreator();
    }

    @Bean
    public ServerStoppedMessageCreator serverStoppedMessageCreator()
    {
        return new ServerStoppedMessageCreator();
    }

    @Bean
    public Map<MessageKind, DiscordMessageCreator> discordMessageCreators(
            DiscordMessageCreator playerJoinedMessageCreator,
            DiscordMessageCreator serverStartingMessageCreator,
            DiscordMessageCreator serverStartedMessageCreator,
            DiscordMessageCreator serverStoppedMessageCreator,
            DiscordMessageCreator serverUpdatedMessageCreator
    )
    {
        return Map.of(
                MessageKind.PLAYER_JOINED, playerJoinedMessageCreator,
                MessageKind.SERVER_STARTING, serverStartingMessageCreator,
                MessageKind.SERVER_STARTED, serverStartedMessageCreator,
                MessageKind.SERVER_STOPPED, serverStoppedMessageCreator,
                MessageKind.SERVER_UPDATED, serverUpdatedMessageCreator
        );
    }

    @Bean
    public DiscordIntegration discordIntegration(
            DiscordWebhookHandler discordWebhookHandler,
            Map<MessageKind, DiscordMessageCreator> discordMessageCreators)
    {
        return new DiscordIntegration(discordWebhookHandler, discordMessageCreators);
    }

    @Bean
    public DiscordWebhookHandler discordWebhookHandler(ASWGConfig aswgConfig,
                                                       ObjectMapper objectMapper,
                                                       RestClient discordRestClient)
    {
        return new DiscordWebhookHandler(aswgConfig.getDiscordWebhookUrl(), objectMapper, discordRestClient);
    }
}
