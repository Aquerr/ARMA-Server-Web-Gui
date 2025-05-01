package pl.bartlomiejstepien.armaserverwebgui.web;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionLogsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.LoggingService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.ProcessService;

@RestController
@RequestMapping("/api/v1/logging")
@AllArgsConstructor
public class LoggingRestController
{
    private final LoggingService loggingService;
    private final ProcessService processService;

    @GetMapping("/properties")
    public LoggingProperties getLoggingProperties()
    {
        return toViewResponse(loggingService.getLoggingProperties());
    }

    @PostMapping("/properties")
    public ResponseEntity<?> saveLoggingProperties(@RequestBody LoggingProperties loggingProperties)
    {
        this.loggingService.saveLoggingProperties(toDomainModel(loggingProperties));
        return ResponseEntity.ok().build();
    }

    @HasPermissionLogsView
    @GetMapping("/latest-logs")
    public LatestServerLogsResponse getLatestLogs()
    {
        return LatestServerLogsResponse.of(this.processService.getLatestServerLogs());
    }

    @HasPermissionLogsView
    @GetMapping(value = "/logs-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToLogPublisher()
    {
        return this.processService.getServerLogEmitter();
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

    @Value(staticConstructor = "of")
    public static class LatestServerLogsResponse
    {
        List<String> logs;
    }
}
