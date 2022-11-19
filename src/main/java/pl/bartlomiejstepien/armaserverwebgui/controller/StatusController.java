package pl.bartlomiejstepien.armaserverwebgui.controller;

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
import pl.bartlomiejstepien.armaserverwebgui.controller.response.RestErrorResponse;
import pl.bartlomiejstepien.armaserverwebgui.exception.ServerIsAlreadyRunningException;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.model.ServerStatus;
import pl.bartlomiejstepien.armaserverwebgui.service.StatusService;
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
    public Mono<ToggleStatusResponse> toggleServerStatus(@RequestBody() ToggleStatusRequest toggleStatusRequest)
    {
        return Mono.just(toggleStatusRequest)
                .map(request -> request.getRequestedStatus() == ServerStatus.OFFLINE ? this.statusService.stopServer() : this.statusService.startServer())
                .map(serverStarted -> serverStarted ? ToggleStatusResponse.of(ServerStatus.STARTING) : ToggleStatusResponse.of(ServerStatus.OFFLINE));
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
        ServerStatus requestedStatus;
    }

    @Value(staticConstructor = "of")
    private static class ToggleStatusResponse
    {
        ServerStatus newStatus;
    }

    @Value
    private static class StatusResponse
    {
        ServerStatus status;
        List<ArmaServerPlayer> playerList;
    }
}
