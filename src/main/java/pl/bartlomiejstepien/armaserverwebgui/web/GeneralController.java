package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.GeneralService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.ArmaServerParametersGenerator;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ArmaServerParameters;
import pl.bartlomiejstepien.armaserverwebgui.web.request.SaveGeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.web.response.GeneralPropertiesResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple5;

@RestController
@RequestMapping("/api/v1/general")
@AllArgsConstructor
public class GeneralController
{
    private final ASWGConfig aswgConfig;
    private final GeneralService generalService;

    private final ArmaServerParametersGenerator armaServerParametersGenerator;

    @GetMapping("/properties")
    public Mono<GeneralPropertiesResponse> getGeneralProperties()
    {
        return Mono.zip(
                Mono.justOrEmpty(aswgConfig.getServerDirectoryPath()),
                Mono.justOrEmpty(aswgConfig.getModsDirectoryPath()),
                Mono.justOrEmpty(aswgConfig.getServerPort()),
                armaServerParametersGenerator.generateParameters(),
                Mono.just(generalService.getGeneralProperties())
        ).map(this::mapToResponse);
    }

    private GeneralPropertiesResponse mapToResponse(Tuple5<String, String, Integer, ArmaServerParameters, GeneralProperties> tuple)
    {
        return GeneralPropertiesResponse.of(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5());
    }

    @PostMapping("/properties")
    public Mono<ResponseEntity<Void>> saveGeneralProperties(@RequestBody SaveGeneralProperties saveGeneralProperties)
    {
        return Mono.just(saveGeneralProperties)
                .map(properties -> {
                    this.aswgConfig.setServerDirectoryPath(properties.getServerDirectory());
                    this.aswgConfig.setModsDirectoryPath(properties.getModsDirectory());
                    this.aswgConfig.setServerPort(properties.getPort());
                    this.generalService.saveGeneralProperties(GeneralProperties.builder()
                            .hostname(properties.getHostname())
                            .maxPlayers(properties.getMaxPlayers())
                            .motd(properties.getMotd())
                            .motdInterval(properties.getMotdInterval())
                            .persistent(properties.isPersistent())
                            .drawingInMap(properties.isDrawingInMap())
                            .headlessClients(properties.getHeadlessClients())
                            .localClients(properties.getLocalClients())
                            .build());
                    return Mono.empty();
                })
                .then(Mono.fromRunnable(this.aswgConfig::saveToFile))
                .then(Mono.just(ResponseEntity.ok().build()));
    }
}
