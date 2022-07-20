package pl.bartlomiejstepien.armaserverwebgui.storage;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.CfgConfigReader;

import java.io.File;

@Repository
@AllArgsConstructor
public class ServerConfigStorageImpl implements ServerConfigStorage
{
    private final ASWGConfig aswgConfig;
    private final CfgConfigReader armaServerCfgFileHandler;

    @Override
    public ArmaServerConfig getServerConfig()
    {
        return armaServerCfgFileHandler.readConfig(aswgConfig.getServerDirectoryPath() + File.separator + "server.cfg");
    }

    @Override
    public void saveServerConfig(ArmaServerConfig armaServerConfig)
    {
        armaServerCfgFileHandler.saveConfig(armaServerConfig);
    }
}
