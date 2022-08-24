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
                .maxPlayers(armaServerConfig.getMaxPlayers())
                .motd(Arrays.stream(armaServerConfig.getMotd()).toList())
                .build();
    }

    @Override
    public void saveGeneralProperties(GeneralProperties generalProperties)
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        armaServerConfig.setMaxPlayers(generalProperties.getMaxPlayers());
        armaServerConfig.setMotd(generalProperties.getMotd().toArray(new String[0]));
        serverConfigStorage.saveServerConfig(armaServerConfig);
    }
}
