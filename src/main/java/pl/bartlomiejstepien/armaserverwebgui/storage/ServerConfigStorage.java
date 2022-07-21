package pl.bartlomiejstepien.armaserverwebgui.storage;

import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;

public interface ServerConfigStorage
{
    ArmaServerConfig getServerConfig();

    void saveServerConfig(ArmaServerConfig armaServerConfig);
}
