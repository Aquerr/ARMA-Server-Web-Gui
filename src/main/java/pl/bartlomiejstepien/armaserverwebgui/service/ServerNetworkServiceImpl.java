package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.model.NetworkProperties;
import pl.bartlomiejstepien.armaserverwebgui.storage.ServerConfigStorage;

@Service
@AllArgsConstructor
public class ServerNetworkServiceImpl implements ServerNetworkService
{
    private final ServerConfigStorage serverConfigStorage;

    @Override
    public NetworkProperties getNetworkProperties()
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        return NetworkProperties.builder()
                .upnp(Boolean.parseBoolean(armaServerConfig.getUpnp()))
                .maxPing(armaServerConfig.getMaxPing())
                .build();
    }

    @Override
    public void saveNetworkProperties(NetworkProperties networkProperties)
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        armaServerConfig.setMaxPing(networkProperties.getMaxPing());
        armaServerConfig.setUpnp(String.valueOf(networkProperties.isUpnp()));
        serverConfigStorage.saveServerConfig(armaServerConfig);
    }
}
