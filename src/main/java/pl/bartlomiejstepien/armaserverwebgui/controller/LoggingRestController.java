package pl.bartlomiejstepien.armaserverwebgui.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.service.LoggingService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/logging")
@AllArgsConstructor
public class LoggingRestController
{
    private final LoggingService loggingService;

    @GetMapping("/properties")
    public Mono<LoggingProperties> getLoggingProperties()
    {
        return Mono.just(loggingService.getLoggingSectionData())
                .map(this::toViewResponse);
    }

    @PostMapping("/properties")
    public Mono<ResponseEntity<Void>> saveLoggingProperties(@RequestBody LoggingProperties loggingProperties)
    {
        return Mono.just(loggingProperties).map(this::toDomainModel)
                .doOnNext(this.loggingService::saveLoggingSectionData)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    private pl.bartlomiejstepien.armaserverwebgui.model.LoggingProperties toDomainModel(LoggingProperties loggingProperties)
    {
        return pl.bartlomiejstepien.armaserverwebgui.model.LoggingProperties.builder()
                .logFile(loggingProperties.getLogFile())
                .build();
    }

    private LoggingProperties toViewResponse(pl.bartlomiejstepien.armaserverwebgui.model.LoggingProperties loggingProperties)
    {
        return LoggingProperties.builder()
                .logFile(loggingProperties.getLogFile())
                .build();
    }

    @Data
    @Builder
    @Jacksonized
    public static class LoggingProperties
    {
        private String logFile;
    }
}
