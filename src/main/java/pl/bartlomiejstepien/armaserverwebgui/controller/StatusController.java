package pl.bartlomiejstepien.armaserverwebgui.controller;

import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.model.ServerStatus;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController
{
    //TODO: Add StatusService which we will get the server status from...

    @GetMapping
    public Mono<StatusResponse> getServerStatus()
    {
        return Mono.just(new StatusResponse(ServerStatus.OFFLINE, Collections.emptyList()));
    }

    @Value
    private static class StatusResponse
    {
        ServerStatus status;
        List<String> playerList;
    }
}
