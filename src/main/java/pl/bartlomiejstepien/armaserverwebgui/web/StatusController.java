package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionServerStartStop;
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
        return Mono.just(processService.getServerStatus())
                .map(serverStatus -> {
                    if (serverStatus.getStatus() == ServerStatus.Status.ONLINE)
                        return new StatusResponse(serverStatus, processService.getServerPlayers());
                    else return new StatusResponse(serverStatus, List.of());
                });
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
