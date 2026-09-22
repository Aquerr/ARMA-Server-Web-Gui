package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.NetworkConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ServerFiles;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;

@Repository
@RequiredArgsConstructor
public class ServerConfigStorageImpl implements ServerConfigStorage
{
    private final ASWGConfig aswgConfig;
    private final CfgFileHandler cfgFileHandler;
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
            return cfgFileHandler.readConfig(getServerConfigFile(), ArmaServerConfig.class);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getRawServerConfigFileContent()
    {
        try
        {
            return Files.readString(getServerConfigFile().toPath(), StandardCharsets.UTF_8);
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
            cfgFileHandler.saveConfig(getServerConfigFile(), armaServerConfig);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveServerConfigFileContent(String content)
    {
        try
        {
            // Using temp file, check if new content parses properly.
            File file = File.createTempFile("arma3-server-config", ".cfg");
            Files.writeString(file.toPath(), content);
            cfgFileHandler.readConfig(file, ArmaServerConfig.class);
            Files.writeString(getServerConfigFile().toPath(), content, StandardOpenOption.CREATE);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public NetworkConfig getNetworkConfig()
    {
        try
        {
            return cfgFileHandler.readConfig(getBasicNetworkConfigFile(), NetworkConfig.class);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getRawNetworkConfigFileContent()
    {
        try
        {
            return Files.readString(getBasicNetworkConfigFile().toPath(), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveBasicNetworkConfigFileContent(String content)
    {
        try
        {
            // Using temp file, check if new content parses properly.
            File file = File.createTempFile("arma3-network", ".cfg");
            Files.writeString(file.toPath(), content);
            cfgFileHandler.readConfig(file, NetworkConfig.class);
            Files.writeString(getBasicNetworkConfigFile().toPath(), content, StandardOpenOption.CREATE);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void saveNetworkConfig(NetworkConfig networkConfig)
    {
        try
        {
            cfgFileHandler.saveConfig(getBasicNetworkConfigFile(), networkConfig);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private File getServerConfigFile()
    {
        return new File(serverConfigFilePath.get());
    }

    private File getBasicNetworkConfigFile()
    {
        return new File(serverNetworkConfigFilePath.get());
    }
}
