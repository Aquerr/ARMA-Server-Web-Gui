package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.NetworkConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;

public interface ServerConfigStorage
{
    ArmaServerConfig getServerConfig();

    String getRawServerConfigFileContent();

    void saveServerConfig(ArmaServerConfig armaServerConfig);

    void saveServerConfigFileContent(String content, boolean validate) throws ParsingException;

    NetworkConfig getNetworkConfig();

    String getRawNetworkConfigFileContent();

    void saveBasicNetworkConfigFileContent(String content, boolean validate) throws ParsingException;

    void saveNetworkConfig(NetworkConfig networkConfig);
}
