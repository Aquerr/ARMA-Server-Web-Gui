package pl.bartlomiejstepien.armaserverwebgui.application.config;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@PropertySources({
        @PropertySource(value = "classpath:aswg-default-config.properties"),
        @PropertySource(value = "file:aswg-config.properties", ignoreResourceNotFound = true)
})
@Setter
public class ASWGConfig
{
    private static final Path ASWG_CONFIGURATION_FILE_PATH = Paths.get("aswg-config.properties");

    private static final String SERVER_DIRECTORY_PATH_PROPERTY = "aswg.server-directory-path";
    private static final String USERNAME_PROPERTY = "aswg.username";
    private static final String PASSWORD_PROPERTY = "aswg.password";
    private static final String STEAMCMD_PATH = "aswg.steamcmd.path";
    private static final String ACTIVE_MODS = "aswg.active-mods";

    private static final String SERVER_PORT = "aswg.server-port";

//    private Properties configurationProperties = new Properties();

    @Value("${aswg.username}")
    private String username;
    @Value("${aswg.password}")
    private String password;
    @Value("${aswg.server-directory-path:}")
    private String serverDirectoryPath;
    @Value("${aswg.steamcmd.path}")
    private String steamCmdPath;
    @Value("${aswg.active-mods:}")
    private String activeMods;

    @Value("${aswg.server-port}")
    private int serverPort;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationRead() throws IOException
    {
        // Create configuration file on startup
        createConfigFileIfNotExists();
    }

    private void createConfigFileIfNotExists() throws IOException
    {
        if (Files.notExists(ASWG_CONFIGURATION_FILE_PATH))
        {
            Files.createFile(ASWG_CONFIGURATION_FILE_PATH);
            Properties configurationProperties = new Properties();
            configurationProperties.setProperty(SERVER_DIRECTORY_PATH_PROPERTY, this.serverDirectoryPath);
            configurationProperties.setProperty(USERNAME_PROPERTY, this.username);
            configurationProperties.setProperty(PASSWORD_PROPERTY, this.password);
            configurationProperties.setProperty(STEAMCMD_PATH, this.steamCmdPath);
            configurationProperties.setProperty(ACTIVE_MODS, this.activeMods);
            configurationProperties.setProperty(SERVER_PORT, String.valueOf(this.serverPort));

            saveProperties();
        }
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getServerDirectoryPath()
    {
        return serverDirectoryPath;
    }

    public String getSteamCmdPath()
    {
        return this.steamCmdPath;
    }

    public void setServerDirectoryPath(String serverDirectoryPath)
    {
        this.serverDirectoryPath = serverDirectoryPath;
        saveProperties();
    }

    private void saveProperties()
    {
        try(FileWriter fileWriter = new FileWriter(ASWG_CONFIGURATION_FILE_PATH.toFile(), StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter))
        {
            Properties configurationProperties = new Properties();
            configurationProperties.setProperty(SERVER_DIRECTORY_PATH_PROPERTY, this.serverDirectoryPath);
            configurationProperties.setProperty(USERNAME_PROPERTY, this.username);
            configurationProperties.setProperty(PASSWORD_PROPERTY, this.password);
            configurationProperties.setProperty(STEAMCMD_PATH, this.steamCmdPath);
            configurationProperties.setProperty(ACTIVE_MODS, this.activeMods);
            configurationProperties.setProperty(SERVER_PORT, String.valueOf(this.serverPort));
            configurationProperties.store(bufferedWriter, "ASWG Configuration File");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setActiveMods(Set<String> mods)
    {
        this.activeMods = mods.stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(";"));
        saveProperties();
    }

    public Set<String> getMods()
    {
        return Arrays.stream(this.activeMods.split(";"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }

    public int getServerPort()
    {
        return this.serverPort;
    }
}
