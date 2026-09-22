package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.NetworkConfig;

public interface ServerConfigStorage
{
    ArmaServerConfig getServerConfig();

    String getRawServerConfigFileContent();

    void saveServerConfig(ArmaServerConfig armaServerConfig);

    void saveServerConfigFileContent(String content);

    NetworkConfig getNetworkConfig();

    String getRawNetworkConfigFileContent();

    void saveBasicNetworkConfigFileContent(String content);

    void saveNetworkConfig(NetworkConfig networkConfig);
}
