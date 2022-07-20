package pl.bartlomiejstepien.armaserverwebgui.storage;

import pl.bartlomiejstepien.armaserverwebgui.util.cfg.ArmaServerConfig;

public interface ServerConfigStorage
{
    ArmaServerConfig getServerConfig();

    void saveServerConfig(ArmaServerConfig armaServerConfig);
}
