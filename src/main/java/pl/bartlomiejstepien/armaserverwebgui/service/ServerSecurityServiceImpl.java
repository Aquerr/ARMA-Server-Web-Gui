package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.model.ServerSecurity;
import pl.bartlomiejstepien.armaserverwebgui.storage.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;

@Service
@AllArgsConstructor
public class ServerSecurityServiceImpl implements ServerSecurityService
{
    private final ServerConfigStorage serverConfigStorage;

    @Override
    public ServerSecurity getServerSecurity()
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        return ServerSecurity.builder()
                .serverPassword(armaServerConfig.getPassword())
                .serverAdminPassword(armaServerConfig.getPasswordAdmin())
                .serverCommandPassword(armaServerConfig.getServerCommandPassword())
                .battleEye(armaServerConfig.getBattleEye() == 1)
                .verifySignatures(armaServerConfig.getVerifySignatures() == 2)
                .build();
    }

    @Override
    public void saveServerSecurity(ServerSecurity serverSecurity)
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        armaServerConfig.setPassword(serverSecurity.getServerPassword());
        armaServerConfig.setPasswordAdmin(serverSecurity.getServerAdminPassword());
        armaServerConfig.setServerCommandPassword(serverSecurity.getServerCommandPassword());
        armaServerConfig.setBattleEye(serverSecurity.isBattleEye() ? 1 : 0);
        armaServerConfig.setVerifySignatures(serverSecurity.isVerifySignatures() ? 2 : 0);
        serverConfigStorage.saveServerConfig(armaServerConfig);
    }
}
