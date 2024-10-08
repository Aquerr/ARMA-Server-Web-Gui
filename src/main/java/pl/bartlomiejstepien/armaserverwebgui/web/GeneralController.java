package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
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
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

import java.util.List;

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
                Mono.justOrEmpty(aswgConfig.getServerPort()),
                armaServerParametersGenerator.generateParameters(),
                Mono.just(generalService.getGeneralProperties())
        ).map(this::mapToResponse);
    }

    private GeneralPropertiesResponse mapToResponse(Tuple4<String, Integer, ArmaServerParameters, GeneralProperties> tuple)
    {
        return GeneralPropertiesResponse.of(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4());
    }

    @PostMapping("/properties")
    public Mono<ResponseEntity<Void>> saveGeneralProperties(@RequestBody SaveGeneralProperties saveGeneralProperties)
    {
        return Mono.just(saveGeneralProperties).doOnNext(properties -> {
            this.aswgConfig.setServerDirectoryPath(properties.getServerDirectory());
            this.aswgConfig.setServerPort(properties.getPort());
            this.generalService.saveGeneralProperties(GeneralProperties.builder()
                    .hostname(properties.getHostname())
                    .maxPlayers(properties.getMaxPlayers())
                    .motd(properties.getMotd())
                    .motdInterval(properties.getMotdInterval())
                    .persistent(properties.isPersistent())
                    .drawingInMap(properties.isDrawingInMap())
                    .build());
        }).then(Mono.just(ResponseEntity.ok().build()));
    }

    @Value(staticConstructor = "of")
    @Builder
    private static class GeneralPropertiesResponse
    {
        String serverDirectory;
        String commandLineParams;
        String hostname;
        int port;
        int maxPlayers;
        List<String> motd;
        int motdInterval;
        boolean persistent;
        boolean drawingInMap;

        static GeneralPropertiesResponse of(String serverDirectory, Integer port, ArmaServerParameters armaServerParameters, GeneralProperties generalProperties)
        {
            return GeneralPropertiesResponse.builder()
                    .serverDirectory(serverDirectory)
                    .commandLineParams(armaServerParameters.asString())
                    .port(port)
                    .hostname(generalProperties.getHostname())
                    .maxPlayers(generalProperties.getMaxPlayers())
                    .motd(generalProperties.getMotd())
                    .motdInterval(generalProperties.getMotdInterval())
                    .persistent(generalProperties.isPersistent())
                    .drawingInMap(generalProperties.isDrawingInMap())
                    .build();
        }
    }

    @Data
    private static class SaveGeneralProperties
    {
        private String hostname;
        private String serverDirectory;
        private int port;
        private int maxPlayers;
        private List<String> motd;
        private int motdInterval;
        private boolean persistent;
        private boolean drawingInMap;
    }
}
