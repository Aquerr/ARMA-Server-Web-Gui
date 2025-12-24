package pl.bartlomiejstepien.armaserverwebgui.web;

import io.github.aquerr.steamwebapiclient.SteamWebApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionSteamSettingsUpdate;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamWebApiClientWrapper;
import pl.bartlomiejstepien.armaserverwebgui.web.request.PasswordChangeRequest;

@RestController
@RequestMapping("/api/v1/settings/steam")
@RequiredArgsConstructor
public class SteamSettingsController
{
    private final ASWGConfig aswgConfig;
    private final SteamWebApiClientWrapper webApiClientWrapper;

    @GetMapping
    @HasPermissionSteamSettingsUpdate
    public SteamSettings getSettings()
    {
        return new SteamSettings(
                aswgConfig.getSteamCmdPath(),
                aswgConfig.getSteamCmdUsername(),
                null,
                aswgConfig.getSteamCmdWorkshopContentPath(),
                aswgConfig.getSteamApiKey()
        );
    }

    @PostMapping
    @HasPermissionSteamSettingsUpdate
    public void save(@RequestBody SteamSettings settings)
    {
        aswgConfig.setSteamCmdPath(settings.steamCmdPath());
        aswgConfig.setSteamCmdUsername(settings.steamCmdUsername());
        aswgConfig.setSteamCmdWorkshopContentPath(settings.steamCmdWorkshopContentPath());

        if (!aswgConfig.getSteamApiKey().equals(settings.steamWebApiToken()))
        {
            recreateSteamWebApiClient(settings.steamWebApiToken());
        }
        aswgConfig.setSteamApiKey(settings.steamWebApiToken());

        this.aswgConfig.saveToFile();
    }

    @HasPermissionSteamSettingsUpdate
    @PostMapping("/password-change")
    public void updateUserPassword(@RequestBody PasswordChangeRequest passwordChange)
    {
        this.aswgConfig.setSteamCmdPassword(passwordChange.getPassword());
        this.aswgConfig.saveToFile();
    }

    private void recreateSteamWebApiClient(String newSteamWebApiToken)
    {
        webApiClientWrapper.setSteamWebApiClient(SteamWebApiClient.builder()
                .apiKey(newSteamWebApiToken)
                .build());
    }

    public record SteamSettings(
            String steamCmdPath,
            String steamCmdUsername,
            String steamCmdPassword,
            String steamCmdWorkshopContentPath,
            String steamWebApiToken)
    {
    }
}
