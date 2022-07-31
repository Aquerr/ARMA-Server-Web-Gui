package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.storage.ServerConfigStorage;

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
                .maxPlayers(armaServerConfig.getMaxPlayers())
                .build();
    }

    @Override
    public void saveGeneralProperties(GeneralProperties generalProperties)
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        armaServerConfig.setMaxPlayers(generalProperties.getMaxPlayers());
        serverConfigStorage.saveServerConfig(armaServerConfig);
    }
}
