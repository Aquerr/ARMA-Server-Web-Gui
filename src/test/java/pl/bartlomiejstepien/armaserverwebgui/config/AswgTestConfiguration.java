package pl.bartlomiejstepien.armaserverwebgui.config;

import io.github.aquerr.steamwebapiclient.SteamWebApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

@TestConfiguration(proxyBeanMethods = false)
public class AswgTestConfiguration
{
    @Value("${wiremock.server.port}")
    protected int wireMockPort;

    @Lazy
    @Bean
    @Primary
    public SteamWebApiClient testSteamWebApiClient()
    {
        return SteamWebApiClient.builder()
                .baseUrl("http://localhost:" + wireMockPort)
                .build();
    }
}
