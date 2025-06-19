package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionSecuritySettingsSave;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionSecuritySettingsView;

@RestController
@RequestMapping("/api/v1/settings/steam")
@RequiredArgsConstructor
public class SteamSettingsController
{
    private final ASWGConfig aswgConfig;

    @GetMapping
    @HasPermissionSecuritySettingsView
    public SteamSettings getSettings()
    {
        return new SteamSettings(
                aswgConfig.getSteamCmdPath(),
                aswgConfig.getSteamCmdUsername(),
                aswgConfig.getSteamCmdPassword(),
                aswgConfig.getSteamCmdWorkshopContentPath(),
                aswgConfig.getSteamApiKey()
        );
    }

    @PostMapping
    @HasPermissionSecuritySettingsSave
    public void save(@RequestBody SteamSettings settings)
    {
        aswgConfig.setSteamCmdPath(settings.steamCmdPath());
        aswgConfig.setSteamCmdUsername(settings.steamCmdUsername());
        aswgConfig.setSteamCmdPassword(settings.steamCmdPassword());
        aswgConfig.setSteamCmdWorkshopContentPath(settings.steamCmdWorkshopContentPath());
        aswgConfig.setSteamApiKey(settings.steamWebApiToken());
        this.aswgConfig.saveToFile();
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
