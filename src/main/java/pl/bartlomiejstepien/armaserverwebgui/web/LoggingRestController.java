package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.LoggingService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.ProcessService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/logging")
@AllArgsConstructor
public class LoggingRestController
{
    private final LoggingService loggingService;
    private final ProcessService processService;

    @GetMapping("/properties")
    public Mono<LoggingProperties> getLoggingProperties()
    {
        return Mono.just(loggingService.getLoggingProperties())
                .map(this::toViewResponse);
    }

    @PostMapping("/properties")
    public Mono<ResponseEntity<Void>> saveLoggingProperties(@RequestBody LoggingProperties loggingProperties)
    {
        return Mono.just(loggingProperties).map(this::toDomainModel)
                .doOnNext(this.loggingService::saveLoggingProperties)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @GetMapping(value = "/logs-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getLogs()
    {
        return Flux.from(this.processService.getServerLogPublisher());
    }

    private pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.model.LoggingProperties toDomainModel(LoggingProperties loggingProperties)
    {
        return pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.model.LoggingProperties.builder()
                .logFile(loggingProperties.getLogFile())
                .build();
    }

    private LoggingProperties toViewResponse(pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.model.LoggingProperties loggingProperties)
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
