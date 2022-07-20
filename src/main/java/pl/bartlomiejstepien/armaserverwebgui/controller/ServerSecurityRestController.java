package pl.bartlomiejstepien.armaserverwebgui.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.model.ServerSecurity;
import pl.bartlomiejstepien.armaserverwebgui.service.ServerSecurityService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/security")
@AllArgsConstructor
public class ServerSecurityRestController
{
    private final ServerSecurityService serverSecurityService;

    @GetMapping
    public Mono<GetServerSecurity> getServerSecurity()
    {
        return Mono.just(this.serverSecurityService.getServerSecurity())
                .map(this::toViewResponse);
    }

    private GetServerSecurity toViewResponse(ServerSecurity serverSecurity)
    {
        return GetServerSecurity.builder()
                .serverPassword(serverSecurity.getServerPassword())
                .serverAdminPassword(serverSecurity.getServerAdminPassword())
                .serverCommandPassword(serverSecurity.getServerCommandPassword())
                .build();
    }

    @Data
    @Builder
    private static class GetServerSecurity
    {
        private String serverPassword;
        private String serverAdminPassword;
        private String serverCommandPassword;
    }
}
