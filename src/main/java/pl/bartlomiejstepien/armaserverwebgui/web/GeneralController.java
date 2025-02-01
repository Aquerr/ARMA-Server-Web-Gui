package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionGeneralSettingsSave;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionGeneralSettingsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.GeneralService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.ArmaServerParametersGenerator;
import pl.bartlomiejstepien.armaserverwebgui.web.request.SaveGeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.web.response.GeneralPropertiesResponse;

@RestController
@RequestMapping("/api/v1/general")
@AllArgsConstructor
public class GeneralController
{
    private final ASWGConfig aswgConfig;
    private final GeneralService generalService;
    private final ArmaServerParametersGenerator armaServerParametersGenerator;

    @HasPermissionGeneralSettingsView
    @GetMapping("/properties")
    public GeneralPropertiesResponse getGeneralProperties()
    {
        return GeneralPropertiesResponse.of(
                this.aswgConfig.getServerDirectoryPath(),
                this.aswgConfig.getModsDirectoryPath(),
                this.aswgConfig.getServerPort(),
                armaServerParametersGenerator.generateParameters(),
                this.generalService.getGeneralProperties()
        );
    }

    @HasPermissionGeneralSettingsSave
    @PostMapping("/properties")
    public ResponseEntity<?> saveGeneralProperties(@RequestBody SaveGeneralProperties saveGeneralProperties)
    {
        this.aswgConfig.setServerDirectoryPath(saveGeneralProperties.getServerDirectory());
        this.aswgConfig.setModsDirectoryPath(saveGeneralProperties.getModsDirectory());
        this.aswgConfig.setServerPort(saveGeneralProperties.getPort());
        this.generalService.saveGeneralProperties(GeneralProperties.builder()
                .hostname(saveGeneralProperties.getHostname())
                .maxPlayers(saveGeneralProperties.getMaxPlayers())
                .motd(saveGeneralProperties.getMotd())
                .motdInterval(saveGeneralProperties.getMotdInterval())
                .persistent(saveGeneralProperties.isPersistent())
                .drawingInMap(saveGeneralProperties.isDrawingInMap())
                .headlessClients(saveGeneralProperties.getHeadlessClients())
                .localClients(saveGeneralProperties.getLocalClients())
                .build());

        this.aswgConfig.saveToFile();
        return ResponseEntity.ok().build();
    }
}
