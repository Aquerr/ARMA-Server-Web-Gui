package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionServerStartStop;
import pl.bartlomiejstepien.armaserverwebgui.web.response.RestErrorResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception.ServerIsAlreadyRunningException;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.ProcessService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/status")
@RequiredArgsConstructor
public class StatusController
{
    private final ProcessService processService;

    @GetMapping
    public Mono<StatusResponse> getServerStatus()
    {
        return Mono.just(new StatusResponse(processService.getServerStatus(), processService.getServerPlayers()));
    }

    @HasPermissionServerStartStop
    @PostMapping("/toggle")
    public Mono<Void> toggleServerStatus(@RequestBody() ToggleStatusRequest toggleStatusRequest)
    {
        return Mono.just(toggleStatusRequest)
                .flatMap(request ->
                {
                    if (request.getRequestedStatus() == ServerStatus.Status.OFFLINE)
                        return this.processService.stopServer();
                    else
                        return this.processService.startServer(toggleStatusRequest.isPerformUpdate());
                })
                .then();
    }

    @ExceptionHandler(value = ServerIsAlreadyRunningException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse onNotAllowedFileTypeException(ServerIsAlreadyRunningException exception)
    {
        return RestErrorResponse.of(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Data
    public static class ToggleStatusRequest
    {
        ServerStatus.Status requestedStatus;
        boolean performUpdate;
    }

    @Value
    public static class StatusResponse
    {
        ServerStatus status;
        List<ArmaServerPlayer> playerList;
    }
}
