package pl.bartlomiejstepien.armaserverwebgui.controller;

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
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.service.GeneralService;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@RestController
@RequestMapping("/api/v1/general")
@AllArgsConstructor
public class GeneralController
{
    private final ASWGConfig aswgConfig;
    private final GeneralService generalService;

    @GetMapping("/properties")
    public Mono<GeneralPropertiesResponse> getGeneralProperties()
    {
        return Mono.zip(
                Mono.justOrEmpty(aswgConfig.getServerDirectoryPath()),
                Mono.just(generalService.getGeneralProperties())
        ).map(this::mapToResponse);
    }

    private GeneralPropertiesResponse mapToResponse(Tuple2<String, GeneralProperties> tuple2)
    {
        return GeneralPropertiesResponse.of(tuple2.getT1(), tuple2.getT2());
    }

    @PostMapping("/properties")
    public Mono<ResponseEntity<Void>> saveGeneralProperties(@RequestBody SaveGeneralProperties saveGeneralProperties)
    {
        return Mono.just(saveGeneralProperties).doOnNext(properties -> {
            this.aswgConfig.setServerDirectoryPath(properties.getServerDirectory());
            this.generalService.saveGeneralProperties(GeneralProperties.builder()
                    .hostname(properties.getHostname())
                    .maxPlayers(properties.getMaxPlayers())
                    .motd(properties.getMotd())
                    .motdInterval(properties.getMotdInterval())
                    .persistent(properties.isPersistent())
                    .build());
        }).then(Mono.just(ResponseEntity.ok().build()));
    }

    @Value(staticConstructor = "of")
    @Builder
    private static class GeneralPropertiesResponse
    {
        String hostname;
        String serverDirectory;
        int maxPlayers;
        List<String> motd;
        int motdInterval;
        boolean persistent;

        static GeneralPropertiesResponse of(String serverDirectory, GeneralProperties generalProperties)
        {
            return GeneralPropertiesResponse.builder()
                    .hostname(generalProperties.getHostname())
                    .serverDirectory(serverDirectory)
                    .maxPlayers(generalProperties.getMaxPlayers())
                    .motd(generalProperties.getMotd())
                    .motdInterval(generalProperties.getMotdInterval())
                    .persistent(generalProperties.isPersistent())
                    .build();
        }
    }

    @Data
    private static class SaveGeneralProperties
    {
        private String hostname;
        private String serverDirectory;
        private int maxPlayers;
        private List<String> motd;
        private int motdInterval;
        private boolean persistent;
    }
}
