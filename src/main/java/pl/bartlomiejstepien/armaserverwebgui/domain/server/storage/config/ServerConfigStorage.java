package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;

public interface ServerConfigStorage
{
    ArmaServerConfig getServerConfig();

    void saveServerConfig(ArmaServerConfig armaServerConfig);
}
