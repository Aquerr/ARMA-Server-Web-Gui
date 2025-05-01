package pl.bartlomiejstepien.armaserverwebgui.web;

import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionServerStartStop;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerPlayer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.ProcessService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerStatus;

@RestController
@RequestMapping("/api/v1/status")
@RequiredArgsConstructor
public class StatusController
{
    private final ProcessService processService;

    @GetMapping
    public StatusResponse getServerStatus()
    {
        ServerStatus status = processService.getServerStatus();
        List<ArmaServerPlayer> players = List.of();
        if (status.getStatus() == ServerStatus.Status.ONLINE)
            players = processService.getServerPlayers();
        return new StatusResponse(status, players);
    }

    @HasPermissionServerStartStop
    @PostMapping("/toggle")
    public void toggleServerStatus(@RequestBody() ToggleStatusRequest toggleStatusRequest)
    {
        if (toggleStatusRequest.getRequestedStatus() == ServerStatus.Status.OFFLINE)
        {
            this.processService.stopServer();
        }
        else
        {
            this.processService.startServer(toggleStatusRequest.isPerformUpdate());
        }
    }

    @Data
    public static class ToggleStatusRequest
    {
        ServerStatus.Status requestedStatus;
        boolean performUpdate;
    }

    public record StatusResponse(ServerStatus status, List<ArmaServerPlayer> playerList)
    {
    }
}
