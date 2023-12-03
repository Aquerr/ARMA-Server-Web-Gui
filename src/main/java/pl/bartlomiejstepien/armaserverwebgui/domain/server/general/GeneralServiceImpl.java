package pl.bartlomiejstepien.armaserverwebgui.domain.server.general;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;

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
                .motdInterval(armaServerConfig.getMotdInterval())
                .persistent(armaServerConfig.getPersistent() == 1)
                .drawingInMap(Boolean.parseBoolean(armaServerConfig.getDrawingInMap()))
                .build();
    }

    @Override
    public void saveGeneralProperties(GeneralProperties generalProperties)
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        armaServerConfig.setHostname(generalProperties.getHostname());
        armaServerConfig.setMaxPlayers(generalProperties.getMaxPlayers());
        armaServerConfig.setMotd(generalProperties.getMotd().toArray(new String[0]));
        armaServerConfig.setMotdInterval(generalProperties.getMotdInterval());
        armaServerConfig.setPersistent(generalProperties.isPersistent() ? 1 : 0);
        armaServerConfig.setDrawingInMap(String.valueOf(generalProperties.isDrawingInMap()));
        serverConfigStorage.saveServerConfig(armaServerConfig);
    }
}
