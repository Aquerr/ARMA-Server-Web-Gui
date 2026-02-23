package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.dto.ServerStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerProcessStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerStatusService
{
    private final ProcessService processService;
    private final SteamService steamService;

    public ServerStatus getServerStatus()
    {
        log.info("Fetching server status...");
        ServerProcessStatus serverProcessStatus = processService.getProcessStatus();

        return switch (serverProcessStatus)
        {
            case UPDATING -> ServerStatus.of(ServerStatus.Status.UPDATING, "Updating");
            case STARTING -> ServerStatus.of(ServerStatus.Status.STARTING, "Starting");
            case RUNNING ->
            {
                if (this.steamService.isServerRunning())
                    yield ServerStatus.of(ServerStatus.Status.ONLINE, "Online");
                yield ServerStatus.of(ServerStatus.Status.RUNNING_BUT_NOT_DETECTED_BY_STEAM, "Running but not detected by Steam");
            }
            default -> ServerStatus.of(ServerStatus.Status.OFFLINE, "Offline");
        };
    }
}
