package pl.bartlomiejstepien.armaserverwebgui.application.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Getter
public class ConfigPathProvider
{
    private final Path configPath;

    public ConfigPathProvider(@Value("${aswg.config.dir:.}") String configDirPath)
    {
        this.configPath = Paths.get(configDirPath).resolve("aswg-config.properties");
    }
}
