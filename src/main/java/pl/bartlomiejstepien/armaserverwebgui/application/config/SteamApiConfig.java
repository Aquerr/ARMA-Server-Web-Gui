package pl.bartlomiejstepien.armaserverwebgui.application.config;

import io.github.aquerr.steamwebapiclient.SteamWebApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SteamApiConfig
{
    @Bean
    public SteamWebApiClient steamWebApiClient(@Value("${aswg.steam.web-api-token:}") String steamApiToken)
    {
        return SteamWebApiClient.builder()
                .apiKey(steamApiToken)
                .build();
    }
}
