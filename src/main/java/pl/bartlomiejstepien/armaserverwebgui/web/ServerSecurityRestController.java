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
import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.ServerSecurityService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model.ServerSecurityProperties;
import pl.bartlomiejstepien.armaserverwebgui.web.request.SaveServerSecurityRequest;
import pl.bartlomiejstepien.armaserverwebgui.web.response.ServerSecurityResponse;

@RestController
@RequestMapping("/api/v1/security")
@AllArgsConstructor
public class ServerSecurityRestController
{
    private final ServerSecurityService serverSecurityService;

    @HasPermissionSecuritySettingsView
    @GetMapping
    public ServerSecurityResponse getServerSecurity()
    {
        return toResponseModel(this.serverSecurityService.getServerSecurity());
    }

    @HasPermissionSecuritySettingsSave
    @PostMapping
    public ResponseEntity<?> saveServerSecurity(@RequestBody SaveServerSecurityRequest saveServerSecurityRequest)
    {
        this.serverSecurityService.saveServerSecurity(toDomainModel(saveServerSecurityRequest));
        return ResponseEntity.ok().build();
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
                .allowedPreprocessFileExtensions(request.getAllowedPreprocessFileExtensions())
                .allowedHTMLLoadExtensions(request.getAllowedHTMLLoadExtensions())
                .adminUUIDs(request.getAdminUUIDs())
                .voteCommands(request.getAllowedVoteCmds())
                .kickDuplicate(request.isKickDuplicate())
                .voteThreshold(request.getVoteThreshold())
                .voteMissionPlayers(request.getVoteMissionPlayers())
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
                .allowedPreprocessFileExtensions(properties.getAllowedPreprocessFileExtensions())
                .allowedHTMLLoadExtensions(properties.getAllowedHTMLLoadExtensions())
                .adminUUIDs(properties.getAdminUUIDs())
                .allowedVoteCmds(properties.getVoteCommands())
                .kickDuplicate(properties.isKickDuplicate())
                .voteThreshold(properties.getVoteThreshold())
                .voteMissionPlayers(properties.getVoteMissionPlayers())
                .build();
    }
}
