package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Mod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;

import javax.annotation.Resource;
import java.io.File;
import java.util.stream.Collectors;

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
                .serverDirectory(aswgConfig.getServerDirectoryPath())
                .configPath(aswgConfig.getServerDirectoryPath() + File.separator + "server.cfg")
                .executablePath(aswgConfig.getServerDirectoryPath() + File.separator + serverExecToUse)
                .port(aswgConfig.getServerPort())
                .mods(aswgConfig.getActiveMods().stream()
                        .filter(mod -> !mod.isServerMod())
                        .map(Mod::getName)
                        .collect(Collectors.toSet()))
                .serverMods(aswgConfig.getActiveMods().stream()
                        .filter(Mod::isServerMod)
                        .map(Mod::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    private boolean is64Bit()
    {
        return System.getProperty("os.arch").contains("64");
    }
}
