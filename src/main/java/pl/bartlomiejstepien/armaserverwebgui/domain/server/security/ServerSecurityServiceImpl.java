package pl.bartlomiejstepien.armaserverwebgui.domain.server.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model.ServerSecurityProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;

import java.util.Arrays;
import java.util.stream.Stream;

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
                .allowedFilePatching(armaServerConfig.getAllowedFilePatching())
                .allowedLoadFileExtensions(Arrays.asList(armaServerConfig.getAllowedLoadFileExtensions()))
                .adminUUIDs(Arrays.asList(armaServerConfig.getAdmins()))
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
        armaServerConfig.setAllowedFilePatching(serverSecurityProperties.getAllowedFilePatching());
        armaServerConfig.setAllowedLoadFileExtensions(serverSecurityProperties.getAllowedLoadFileExtensions().toArray(new String[0]));
        armaServerConfig.setAdmins(serverSecurityProperties.getAdminUUIDs().toArray(new String[0]));
        serverConfigStorage.saveServerConfig(armaServerConfig);
    }
}
