package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model.ServerSecurityProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.ServerSecurityService;
import reactor.core.publisher.Mono;

import java.util.List;

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

    private ServerSecurityProperties toDomainModel(SaveServerSecurityRequest saveServerSecurityRequest)
    {
        return ServerSecurityProperties.builder()
                .serverPassword(saveServerSecurityRequest.getServerPassword())
                .serverAdminPassword(saveServerSecurityRequest.getServerAdminPassword())
                .serverCommandPassword(saveServerSecurityRequest.getServerCommandPassword())
                .battleEye(saveServerSecurityRequest.isBattleEye())
                .verifySignatures(saveServerSecurityRequest.isVerifySignatures())
                .allowedFilePatching(saveServerSecurityRequest.getAllowedFilePatching())
                .allowedLoadFileExtensions(saveServerSecurityRequest.getAllowedLoadFileExtensions())
                .build();
    }

    private GetServerSecurity toViewResponse(ServerSecurityProperties serverSecurityProperties)
    {
        return GetServerSecurity.builder()
                .serverPassword(serverSecurityProperties.getServerPassword())
                .serverAdminPassword(serverSecurityProperties.getServerAdminPassword())
                .serverCommandPassword(serverSecurityProperties.getServerCommandPassword())
                .battleEye(serverSecurityProperties.isBattleEye())
                .verifySignatures(serverSecurityProperties.isVerifySignatures())
                .allowedFilePatching(serverSecurityProperties.getAllowedFilePatching())
                .allowedLoadFileExtensions(serverSecurityProperties.getAllowedLoadFileExtensions())
                .build();
    }

    @Data
    @Builder
    private static class SaveServerSecurityRequest
    {
        private String serverPassword;
        private String serverAdminPassword;
        private String serverCommandPassword;
        private boolean battleEye;
        private boolean verifySignatures;
        private int allowedFilePatching;
        private List<String> allowedLoadFileExtensions;
    }

    @Data
    @Builder
    private static class GetServerSecurity
    {
        private String serverPassword;
        private String serverAdminPassword;
        private String serverCommandPassword;
        private boolean battleEye;
        private boolean verifySignatures;
        private int allowedFilePatching;
        private List<String> allowedLoadFileExtensions;
    }
}
