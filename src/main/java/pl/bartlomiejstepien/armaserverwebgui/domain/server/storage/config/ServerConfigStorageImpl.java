package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgConfigReader;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgConfigWriter;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.function.Supplier;

@Repository
@RequiredArgsConstructor
public class ServerConfigStorageImpl implements ServerConfigStorage
{
    private final ASWGConfig aswgConfig;
    private final CfgConfigReader cfgConfigReader;
    private final CfgConfigWriter cfgConfigWriter;
    private Supplier<String> cfgFilePath;

    @PostConstruct
    private void postConstruct()
    {
        this.cfgFilePath = () -> aswgConfig.getServerDirectoryPath() + File.separator + "server.cfg";
    }

    @Override
    public ArmaServerConfig getServerConfig()
    {
        return cfgConfigReader.readConfig(new File(cfgFilePath.get()));
    }

    @Override
    public void saveServerConfig(ArmaServerConfig armaServerConfig)
    {
        cfgConfigWriter.saveConfig(new File(cfgFilePath.get()), armaServerConfig);
    }
}
