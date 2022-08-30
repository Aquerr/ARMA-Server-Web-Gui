package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerParameters;
import pl.bartlomiejstepien.armaserverwebgui.storage.ServerConfigStorage;

import javax.annotation.Resource;
import java.io.File;
import java.util.Collections;

@AllArgsConstructor
@Component
public class ArmaServerParametersGeneratorImpl implements ArmaServerParametersGenerator
{
    @Resource
    private final ServerConfigStorage serverConfigStorage;
    @Resource
    private final ASWGConfig aswgConfig;

    @Override
    public ArmaServerParameters generateParameters()
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();

        String serverExecToUse = is64Bit() ? "arma3server_x64" : "arma3server";

        return ArmaServerParameters.builder()
                .serverName(armaServerConfig.getHostname())
                .configPath(aswgConfig.getServerDirectoryPath() + File.separator + "server.cfg")
                .executablePath(aswgConfig.getServerDirectoryPath() + File.separator + serverExecToUse)
                .port(2302) //TODO: Allow specifying server port
                .mods(Collections.emptyList()) //TODO...
                .build();
    }

    private boolean is64Bit()
    {
        return System.getProperty("os.arch").contains("64");
    }
}
