package pl.bartlomiejstepien.armaserverwebgui.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.model.Server;
import pl.bartlomiejstepien.armaserverwebgui.service.ServerService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/servers")
@AllArgsConstructor
public class ServerRestController
{
    private final ServerService serverService;

    /**
     * Gets servers list. Currently only one server is supported.
     * @return
     */
    @GetMapping
    public Mono<GetServersResponse> getServers()
    {
        return Mono.just(GetServersResponse.of(this.serverService.getServers()));
    }

    @Value
    static class GetServersResponse
    {
        List<ServerView> servers;

        static GetServersResponse of(List<Server> servers)
        {
            return new GetServersResponse(servers.stream().map(ServerView::of)
                    .collect(Collectors.toList()));
        }
    }

    @Builder
    @Value
    private static class ServerView
    {
        static ServerView of(Server server)
        {
            return null;
        }
    }
}
