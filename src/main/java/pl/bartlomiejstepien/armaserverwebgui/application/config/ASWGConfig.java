package pl.bartlomiejstepien.armaserverwebgui.application.config;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModDir;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
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
    private static final String STEAMCMD_USERNAME = "aswg.steamcmd.username";
    private static final String STEAMCMD_PASSWORD = "aswg.steamcmd.password";
    private static final String ACTIVE_MODS = "aswg.active-mod-dirs";
    private static final String ACTIVE_SERVER_MODS = "aswg.active-server-mod-dirs";
    private static final String STEAM_API_KEY = "aswg.steam.web-api-token";

    private static final String SERVER_PORT = "aswg.server-port";

    @Value("${aswg.username}")
    private String username;
    @Value("${aswg.password}")
    private String password;
    @Value("${aswg.server-port}")
    private int serverPort;
    @Value("${aswg.server-directory-path:}")
    private String serverDirectoryPath;

    @Value("${aswg.steamcmd.path}")
    private String steamCmdPath;
    @Value("${aswg.steamcmd.username}")
    private String steamCmdUsername;
    @Value("${aswg.steamcmd.password}")
    private String steamCmdPassword;
    @Value("${aswg.steam.web-api-token:}")
    private String steamApiKey;

    @Value("${aswg.active-mod-dirs}")
    private String activeModDirs;
    @Value("${aswg.active-server-mod-dirs}")
    private String activeServerModDirs;

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
            configurationProperties.setProperty(ACTIVE_MODS, this.activeModDirs);
            configurationProperties.setProperty(ACTIVE_SERVER_MODS, this.activeServerModDirs);
            configurationProperties.setProperty(SERVER_PORT, String.valueOf(this.serverPort));
            configurationProperties.setProperty(STEAM_API_KEY, this.steamApiKey);
            configurationProperties.setProperty(STEAMCMD_USERNAME, this.steamCmdUsername);
            configurationProperties.setProperty(STEAMCMD_PASSWORD, this.steamCmdPassword);

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

    public String getSteamCmdUsername()
    {
        return steamCmdUsername;
    }

    public String getSteamCmdPassword()
    {
        return steamCmdPassword;
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
            configurationProperties.setProperty(ACTIVE_MODS, this.activeModDirs);
            configurationProperties.setProperty(ACTIVE_SERVER_MODS, this.activeServerModDirs);
            configurationProperties.setProperty(SERVER_PORT, String.valueOf(this.serverPort));
            configurationProperties.setProperty(STEAM_API_KEY, this.steamApiKey);
            configurationProperties.setProperty(STEAMCMD_USERNAME, this.steamCmdUsername);
            configurationProperties.setProperty(STEAMCMD_PASSWORD, this.steamCmdPassword);
            configurationProperties.store(bufferedWriter, "ASWG Configuration File");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setActiveModDirs(Set<ModDir> modDirs)
    {
        this.activeModDirs = modDirs.stream()
                .filter(mod -> !mod.isServerMod())
                .map(ModDir::getDirName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(";"));
        this.activeServerModDirs = modDirs.stream()
                .filter(ModDir::isServerMod)
                .map(ModDir::getDirName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(";"));
        saveProperties();
    }

    public Set<ModDir> getActiveModDirs()
    {
        Set<ModDir> allModDirs = new HashSet<>();

        Set<ModDir> modDirs = Arrays.stream(this.activeModDirs.split(";"))
                .filter(StringUtils::isNotBlank)
                .map(modName -> new ModDir(modName, false))
                .collect(Collectors.toSet());

        Set<ModDir> serverModDirs = Arrays.stream(this.activeServerModDirs.split(";"))
                .filter(StringUtils::isNotBlank)
                .map(modName -> new ModDir(modName, true))
                .collect(Collectors.toSet());

        allModDirs.addAll(modDirs);
        allModDirs.addAll(serverModDirs);
        return allModDirs;
    }

    public int getServerPort()
    {
        return this.serverPort;
    }
}
