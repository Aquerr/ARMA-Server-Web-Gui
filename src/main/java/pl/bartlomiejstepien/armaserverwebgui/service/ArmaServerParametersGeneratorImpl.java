package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;

import javax.annotation.Resource;
import java.io.File;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Component
public class ArmaServerParametersGeneratorImpl implements ArmaServerParametersGenerator
{
    @Resource
    private final ArmaServerConfig armaServerConfig;
    @Resource
    private final ASWGConfig aswgConfig;

    @Override
    public String generateParameters()
    {
        return ArmaServerParametersBuilder.builder()
                .serverName(armaServerConfig.getHostname())
                .configPath(aswgConfig.getServerDirectoryPath() + File.separator + "server.cfg")
                .executablePath(aswgConfig.getServerDirectoryPath() + File.separator + "arma3server")
                .port(2302) //TODO: Allow specifying server port
                .mods(Collections.emptyList()) //TODO...
                .build()
                .asString();
    }

    @Builder
    private static class ArmaServerParametersBuilder
    {
        private String configPath;
        private int port;
        private String serverName;
        private String executablePath;
        private List<String> mods;

        String asString()
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(executablePath)
                    .append(" -port=").append(port)
                    .append(" -name=").append(serverName)
                    .append(" -config=").append(configPath)
                    .append(" -mod=").append(String.join(";", mods));
            return stringBuilder.toString();
        }
    }
}
