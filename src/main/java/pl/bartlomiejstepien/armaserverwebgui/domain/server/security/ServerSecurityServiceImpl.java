package pl.bartlomiejstepien.armaserverwebgui.domain.server.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model.ServerSecurityProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model.VoteCommand;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.VoteCmd;

import java.util.Arrays;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ServerSecurityServiceImpl implements ServerSecurityService
{
    private final ServerConfigStorage serverConfigStorage;

    @Override
    public ServerSecurityProperties getServerSecurity()
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        return ServerSecurityProperties.builder()
                .serverPassword(armaServerConfig.getPassword())
                .serverAdminPassword(armaServerConfig.getPasswordAdmin())
                .serverCommandPassword(armaServerConfig.getServerCommandPassword())
                .battleEye(armaServerConfig.getBattleEye() == 1)
                .verifySignatures(armaServerConfig.getVerifySignatures() == 2)
                .allowedFilePatching(Optional.ofNullable(ServerSecurityProperties.AllowedFilePatching.findByConfigValue(armaServerConfig.getAllowedFilePatching()))
                        .orElseThrow(() -> new RuntimeException("Could not find AllowedFilePatching for config value = " + armaServerConfig.getAllowedFilePatching())))
                .filePatchingIgnoredClients(Arrays.stream(armaServerConfig.getFilePatchingExceptions()).toList())
                .allowedLoadFileExtensions(Arrays.asList(armaServerConfig.getAllowedLoadFileExtensions()))
                .adminUUIDs(Arrays.asList(armaServerConfig.getAdmins()))
                .voteCommands(Optional.ofNullable(armaServerConfig.getAllowedVoteCmds())
                        .map(cmds -> Arrays.stream(cmds)
                                .map(voteCmd -> VoteCommand.builder()
                                        .name(voteCmd.getCommandName())
                                        .allowedPreMission(voteCmd.isPreMissionStart())
                                        .allowedPostMission(voteCmd.isPostMissionStart())
                                        .votingThreshold(voteCmd.getVotingThreshold())
                                        .percentageSideVotingThreshold(voteCmd.getPercentSideVotingThreshold())
                                        .build())
                        .toList())
                        .orElse(null))
                .build();
    }

    @Override
    public void saveServerSecurity(ServerSecurityProperties serverSecurityProperties)
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        armaServerConfig.setPassword(serverSecurityProperties.getServerPassword());
        armaServerConfig.setPasswordAdmin(serverSecurityProperties.getServerAdminPassword());
        armaServerConfig.setServerCommandPassword(serverSecurityProperties.getServerCommandPassword());
        armaServerConfig.setBattleEye(serverSecurityProperties.isBattleEye() ? 1 : 0);
        armaServerConfig.setVerifySignatures(serverSecurityProperties.isVerifySignatures() ? 2 : 0);
        armaServerConfig.setAllowedFilePatching(serverSecurityProperties.getAllowedFilePatching().getConfigValue());
        armaServerConfig.setFilePatchingExceptions(serverSecurityProperties.getFilePatchingIgnoredClients().toArray(new String[0]));
        armaServerConfig.setAllowedLoadFileExtensions(serverSecurityProperties.getAllowedLoadFileExtensions().toArray(new String[0]));
        armaServerConfig.setAdmins(serverSecurityProperties.getAdminUUIDs().toArray(new String[0]));
        armaServerConfig.setAllowedVoteCmds(Optional.ofNullable(serverSecurityProperties.getVoteCommands())
                .map(voteCommands -> voteCommands.stream()
                    .map(voteCommand -> VoteCmd.builder()
                            .commandName(voteCommand.getName())
                            .preMissionStart(voteCommand.isAllowedPreMission())
                            .postMissionStart(voteCommand.isAllowedPostMission())
                            .votingThreshold(voteCommand.getVotingThreshold())
                            .percentSideVotingThreshold(voteCommand.getPercentageSideVotingThreshold())
                            .build())
                    .toArray(VoteCmd[]::new))
                .orElse(null));
        serverConfigStorage.saveServerConfig(armaServerConfig);
    }
}
