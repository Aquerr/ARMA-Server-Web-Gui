package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.storage.ServerConfigStorage;

import java.util.Arrays;

@Service
@AllArgsConstructor
public class GeneralServiceImpl implements GeneralService
{
    private final ServerConfigStorage serverConfigStorage;

    @Override
    public GeneralProperties getGeneralProperties()
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        return GeneralProperties.builder()
                .hostname(armaServerConfig.getHostname())
                .maxPlayers(armaServerConfig.getMaxPlayers())
                .motd(Arrays.stream(armaServerConfig.getMotd()).toList())
                .persistent(armaServerConfig.getPersistent() == 1)
                .build();
    }

    @Override
    public void saveGeneralProperties(GeneralProperties generalProperties)
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        armaServerConfig.setHostname(generalProperties.getHostname());
        armaServerConfig.setMaxPlayers(generalProperties.getMaxPlayers());
        armaServerConfig.setMotd(generalProperties.getMotd().toArray(new String[0]));
        armaServerConfig.setPersistent(generalProperties.isPersistent() ? 1 : 0);
        serverConfigStorage.saveServerConfig(armaServerConfig);
    }
}
