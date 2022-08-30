package pl.bartlomiejstepien.armaserverwebgui.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.armaserverwebgui.model.ServerStatus;
import pl.bartlomiejstepien.armaserverwebgui.service.StatusService;
import reactor.core.publisher.Mono;

import java.util.Collections;
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
        return Mono.just(new StatusResponse(statusService.getServerStatus(), Collections.emptyList()));
    }

    @PostMapping("/toggle")
    public Mono<ToggleStatusResponse> toggleServerStatus(@RequestBody() ToggleStatusRequest toggleStatusRequest)
    {
        return Mono.just(toggleStatusRequest)
                .map(request -> request.getRequestedStatus() == ServerStatus.OFFLINE ? this.statusService.stopServer() : this.statusService.startServer())
                .map(serverStarted -> serverStarted ? ToggleStatusResponse.of(ServerStatus.ONLINE) : ToggleStatusResponse.of(ServerStatus.OFFLINE));
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
        List<String> playerList;
    }
}
