package pl.bartlomiejstepien.armaserverwebgui.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.service.GeneralService;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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
                    .maxPlayers(properties.getMaxPlayers())
                    .build());
        }).then(Mono.just(ResponseEntity.ok().build()));
    }

    @Value(staticConstructor = "of")
    @Builder
    private static class GeneralPropertiesResponse
    {
        String serverDirectory;
        int maxPlayers;

        static GeneralPropertiesResponse of(String serverDirectory, GeneralProperties generalProperties)
        {
            return GeneralPropertiesResponse.builder()
                    .serverDirectory(serverDirectory)
                    .maxPlayers(generalProperties.getMaxPlayers())
                    .build();
        }
    }

    @Data
    private static class SaveGeneralProperties
    {
        private String serverDirectory;
        private int maxPlayers;
    }
}
