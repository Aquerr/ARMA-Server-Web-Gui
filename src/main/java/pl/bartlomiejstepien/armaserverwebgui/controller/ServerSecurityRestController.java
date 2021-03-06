package pl.bartlomiejstepien.armaserverwebgui.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public Mono<ResponseEntity<?>> saveServerSecurity(@RequestBody SaveServerSecurityRequest saveServerSecurityRequest)
    {
        this.serverSecurityService.saveServerSecurity(toDomainModel(saveServerSecurityRequest));
        return Mono.just(ResponseEntity.ok().build());
    }

    private ServerSecurity toDomainModel(SaveServerSecurityRequest saveServerSecurityRequest)
    {
        return ServerSecurity.builder()
                .serverPassword(saveServerSecurityRequest.getServerPassword())
                .serverAdminPassword(saveServerSecurityRequest.getServerAdminPassword())
                .serverCommandPassword(saveServerSecurityRequest.getServerCommandPassword())
                .build();
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
    private static class SaveServerSecurityRequest
    {
        private String serverPassword;
        private String serverAdminPassword;
        private String serverCommandPassword;
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
