package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.DefaultCfgConfigReader;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.DefaultCfgConfigWriter;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

@Repository
@RequiredArgsConstructor
public class ServerConfigStorageImpl implements ServerConfigStorage
{
    private final ASWGConfig aswgConfig;
    private final CfgFileHandler cfgFileHandler = new CfgFileHandler(
            DefaultCfgConfigReader.INSTNACE,
            DefaultCfgConfigWriter.INSTANCE
    );
    private Supplier<String> cfgFilePath;

    @PostConstruct
    private void postConstruct()
    {
        this.cfgFilePath = () -> aswgConfig.getServerDirectoryPath() + File.separator + "server.cfg";
    }

    @Override
    public ArmaServerConfig getServerConfig()
    {
        try
        {
            return cfgFileHandler.readConfig(new File(cfgFilePath.get()), ArmaServerConfig.class);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveServerConfig(ArmaServerConfig armaServerConfig)
    {
        try
        {
            cfgFileHandler.saveConfig(new File(cfgFilePath.get()), armaServerConfig);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
