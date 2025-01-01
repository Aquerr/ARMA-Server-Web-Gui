package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionSecuritySettingsSave;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionSecuritySettingsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model.ServerSecurityProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.ServerSecurityService;
import pl.bartlomiejstepien.armaserverwebgui.web.request.SaveServerSecurityRequest;
import pl.bartlomiejstepien.armaserverwebgui.web.response.ServerSecurityResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/security")
@AllArgsConstructor
public class ServerSecurityRestController
{
    private final ServerSecurityService serverSecurityService;

    @HasPermissionSecuritySettingsView
    @GetMapping
    public Mono<ServerSecurityResponse> getServerSecurity()
    {
        return Mono.just(this.serverSecurityService.getServerSecurity())
                .map(this::toResponseModel);
    }

    @HasPermissionSecuritySettingsSave
    @PostMapping
    public Mono<ResponseEntity<?>> saveServerSecurity(@RequestBody SaveServerSecurityRequest saveServerSecurityRequest)
    {
        this.serverSecurityService.saveServerSecurity(toDomainModel(saveServerSecurityRequest));
        return Mono.just(ResponseEntity.ok().build());
    }

    private ServerSecurityProperties toDomainModel(SaveServerSecurityRequest request)
    {
        return ServerSecurityProperties.builder()
                .serverPassword(request.getServerPassword())
                .serverAdminPassword(request.getServerAdminPassword())
                .serverCommandPassword(request.getServerCommandPassword())
                .battleEye(request.isBattleEye())
                .verifySignatures(request.isVerifySignatures())
                .allowedFilePatching(request.getAllowedFilePatching())
                .filePatchingIgnoredClients(request.getFilePatchingIgnoredClients())
                .allowedLoadFileExtensions(request.getAllowedLoadFileExtensions())
                .adminUUIDs(request.getAdminUUIDs())
                .voteCommands(request.getAllowedVoteCmds())
                .build();
    }

    private ServerSecurityResponse toResponseModel(ServerSecurityProperties properties)
    {
        return ServerSecurityResponse.builder()
                .serverPassword(properties.getServerPassword())
                .serverAdminPassword(properties.getServerAdminPassword())
                .serverCommandPassword(properties.getServerCommandPassword())
                .battleEye(properties.isBattleEye())
                .verifySignatures(properties.isVerifySignatures())
                .allowedFilePatching(properties.getAllowedFilePatching())
                .filePatchingIgnoredClients(properties.getFilePatchingIgnoredClients())
                .allowedLoadFileExtensions(properties.getAllowedLoadFileExtensions())
                .adminUUIDs(properties.getAdminUUIDs())
                .allowedVoteCmds(properties.getVoteCommands())
                .build();
    }
}
