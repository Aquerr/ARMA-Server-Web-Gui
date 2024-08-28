package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.NetworkConfig;

public interface ServerConfigStorage
{
    ArmaServerConfig getServerConfig();

    void saveServerConfig(ArmaServerConfig armaServerConfig);

    NetworkConfig getNetworkConfig();

    void saveNetworkConfig(NetworkConfig networkConfig);
}
