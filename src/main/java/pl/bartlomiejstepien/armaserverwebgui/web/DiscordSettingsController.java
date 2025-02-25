package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;

@RestController
@RequestMapping("/api/v1/settings/discord")
@RequiredArgsConstructor
public class DiscordSettingsController
{
    private final ASWGConfig aswgConfig;

    @GetMapping
    public DiscordSettings getSettings()
    {
        ASWGConfig.DiscordProperties discordProperties = aswgConfig.getDiscordProperties();
        return new DiscordSettings(
                discordProperties.isEnabled(),
                discordProperties.getWebhookUrl(),
                discordProperties.getMessageServerStarting(),
                discordProperties.getMessageServerStart(),
                discordProperties.getMessageServerStop(),
                discordProperties.getMessageServerUpdate()
        );
    }

    @PostMapping
    public void save(@RequestBody DiscordSettings settings)
    {
        ASWGConfig.DiscordProperties discordProperties = this.aswgConfig.getDiscordProperties();
        discordProperties.setEnabled(settings.enabled());
        discordProperties.setWebhookUrl(settings.webhookUrl());
        discordProperties.setMessageServerStarting(settings.serverStartingMessage());
        discordProperties.setMessageServerStart(settings.serverStartMessage());
        discordProperties.setMessageServerStop(settings.serverStopMessage());
        discordProperties.setMessageServerUpdate(settings.serverUpdateMessage());
        this.aswgConfig.saveToFile();
    }

    public record DiscordSettings(boolean enabled,
                                  String webhookUrl,
                                  String serverStartingMessage,
                                  String serverStartMessage,
                                  String serverStopMessage,
                                  String serverUpdateMessage)
    { }
}
