package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.NetworkConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ServerFiles;
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
    private Supplier<String> serverConfigFilePath;
    private Supplier<String> serverNetworkConfigFilePath;

    @PostConstruct
    private void postConstruct()
    {
        this.serverConfigFilePath = () -> aswgConfig.getServerDirectoryPath() + File.separator + ServerFiles.SERVER_CONFIG;
        this.serverNetworkConfigFilePath = () -> aswgConfig.getServerDirectoryPath() + File.separator + ServerFiles.NETWORK_CONFIG;
    }

    @Override
    public ArmaServerConfig getServerConfig()
    {
        try
        {
            return cfgFileHandler.readConfig(new File(serverConfigFilePath.get()), ArmaServerConfig.class);
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
            cfgFileHandler.saveConfig(new File(serverConfigFilePath.get()), armaServerConfig);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NetworkConfig getNetworkConfig()
    {
        try
        {
            return cfgFileHandler.readConfig(new File(serverNetworkConfigFilePath.get()), NetworkConfig.class);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveNetworkConfig(NetworkConfig networkConfig)
    {
        try
        {
            cfgFileHandler.saveConfig(new File(serverNetworkConfigFilePath.get()), networkConfig);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
