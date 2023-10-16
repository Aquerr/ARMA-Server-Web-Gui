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
import pl.bartlomiejstepien.armaserverwebgui.web.response.RestErrorResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception.ServerIsAlreadyRunningException;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.StatusService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/status")
@RequiredArgsConstructor
public class StatusController
{
    private final StatusService statusService;

    @GetMapping
    public Mono<StatusResponse> getServerStatus()
    {
        return Mono.just(new StatusResponse(statusService.getServerStatus(), statusService.getServerPlayers()));
    }

    @PostMapping("/toggle")
    public Mono<Void> toggleServerStatus(@RequestBody() ToggleStatusRequest toggleStatusRequest)
    {
        return Mono.just(toggleStatusRequest)
                .doOnSuccess(request ->
                {
                    if (request.getRequestedStatus() == ServerStatus.Status.OFFLINE)
                        this.statusService.stopServer();
                    else
                        this.statusService.startServer(toggleStatusRequest.isPerformUpdate());
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
    private static class ToggleStatusRequest
    {
        ServerStatus.Status requestedStatus;
        boolean performUpdate;
    }

    @Value
    private static class StatusResponse
    {
        ServerStatus status;
        List<ArmaServerPlayer> playerList;
    }
}
