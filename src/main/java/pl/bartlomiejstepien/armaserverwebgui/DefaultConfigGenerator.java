package pl.bartlomiejstepien.armaserverwebgui;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class DefaultConfigGenerator
{
    @PostConstruct
    public void postConstruct() throws IOException
    {
        Path configFilePath = Paths.get(".").resolve("aswg-config.properties");
        if (Files.exists(configFilePath))
            return;

        log.info("Creating default config file in: {}", configFilePath);
        Files.createDirectories(configFilePath.getParent());
        Files.write(configFilePath, new ClassPathResource("aswg-default-config.properties").getContentAsByteArray(), StandardOpenOption.CREATE);
    }
}
