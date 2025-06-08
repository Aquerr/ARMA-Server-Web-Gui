package pl.bartlomiejstepien.armaserverwebgui;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class DefaultConfigGenerator
{
    @Value("${aswg.config.dir:.}")
    private String configDirPath;

    @Autowired
    private ConfigurableEnvironment environment;

    @PostConstruct
    public void postConstruct() throws IOException
    {
        Path configFilePath = Paths.get(this.configDirPath).resolve("aswg-config.properties");
        if (Files.exists(configFilePath))
            return;

        log.info("Creating default config file in: {}", configFilePath);
        Files.createDirectories(configFilePath.getParent());
        Files.write(configFilePath, new ClassPathResource("aswg-default-config.properties").getContentAsByteArray(), StandardOpenOption.CREATE);
        environment.getPropertySources().addFirst(
                new PropertiesPropertySource("aswg-config", PropertiesLoaderUtils.loadProperties(new FileSystemResource(configFilePath)))
        );
    }
}
