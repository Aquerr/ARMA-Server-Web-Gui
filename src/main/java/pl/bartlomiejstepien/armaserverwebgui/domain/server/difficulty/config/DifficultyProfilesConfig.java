package pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Lazy;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.SystemUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration(proxyBeanMethods = false)
public class DifficultyProfilesConfig
{
    @Bean(defaultCandidate = false)
    public Lazy<Path> profilesDirectory(ASWGConfig aswgConfig)
    {
        if (SystemUtils.isWindows())
        {
            return Lazy.of(() -> Paths.get(aswgConfig.getServerDirectoryPath()).resolve("aswg_profiles"));
        }
        else
        {
            return Lazy.of(() -> Paths.get(System.getProperty("user.home"))
                    .resolve(".local")
                    .resolve("share")
                    .resolve("Arma 3 - Other Profiles"));
        }
    }
}
