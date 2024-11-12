package pl.bartlomiejstepien.armaserverwebgui.application.config;

import lombok.Setter;
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
import java.util.Properties;

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
    private static final String STEAM_API_KEY = "aswg.steam.web-api-token";

    private static final String SERVER_PORT = "aswg.server-port";

    private static final String FILE_SCANNER_INSTALLATION_ENABLED_PROPERTY = "aswg.job.file-scanner.installation.enabled";
    private static final String FILE_SCANNER_DELETION_ENABLED_PROPERTY = "aswg.job.file-scanner.deletion.enabled";
    private static final String DIFFICULTY_PROFILE_INSTALLATION_SCANNER_ENABLED = "aswg.job.difficulty-scanner.installation.enabled";
    private static final String MOD_SETTINGS_INSTLLATION_SCANNER_ENABLED = "aswg.job.mod-settings-scanner.installation.enabled";
    private static final String VANILLA_MISSIONS_IMPORTER = "aswg.vanilla-missions-importer.enabled";
    private static final String DISCORD_WEBHOOK_ENABLED = "aswg.discord.webhook.enabled";
    private static final String DISCORD_WEBHOOK_URL = "aswg.discord.webhook.url";


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
    @Value("${aswg.job.file-scanner.installation.enabled:true}")
    private boolean fileScannerInstallationEnabled;
    @Value("${aswg.job.file-scanner.deletion.enabled:false}")
    private boolean fileScannerDeletionEnabled;

    @Value("${aswg.job.difficulty-scanner.installation.enabled}")
    private boolean difficultyProfileInstallationScannerEnabled;

    @Value("${aswg.job.mod-settings-scanner.installation.enabled}")
    private boolean modSettingsInstallationScannerEnabled;

    @Value("${aswg.vanilla-missions-importer.enabled}")
    private boolean vanillaMissionsImporter;

    @Value("${aswg.discord.webhook.enabled}")
    private boolean discordWebhookEnabled;

    @Value("${aswg.discord.webhook.url}")
    private String discordWebhookUrl;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationRead() throws IOException
    {
        // Create configuration file on startup
        createConfigFileIfNotExists();
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

    public int getServerPort()
    {
        return this.serverPort;
    }

    public boolean isFileScannerDeletionEnabled()
    {
        return fileScannerDeletionEnabled;
    }

    public boolean isFileScannerInstallationEnabled()
    {
        return fileScannerInstallationEnabled;
    }

    public boolean isDifficultyProfileInstallationScannerEnabled()
    {
        return difficultyProfileInstallationScannerEnabled;
    }

    public boolean isModSettingsInstallationScannerEnabled()
    {
        return this.modSettingsInstallationScannerEnabled;
    }

    public String getDiscordWebhookUrl()
    {
        return this.discordWebhookUrl;
    }

    public boolean isDiscordWebhookEnabled()
    {
        return this.discordWebhookEnabled;
    }

    private void saveProperties()
    {
        try(FileWriter fileWriter = new FileWriter(ASWG_CONFIGURATION_FILE_PATH.toFile(), StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter))
        {
            Properties configurationProperties = prepareProperties();
            configurationProperties.store(bufferedWriter, "ASWG Configuration File");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void createConfigFileIfNotExists() throws IOException
    {
        if (Files.notExists(ASWG_CONFIGURATION_FILE_PATH))
        {
            Files.createFile(ASWG_CONFIGURATION_FILE_PATH);
            saveProperties();
        }
    }

    private Properties prepareProperties()
    {
        Properties configurationProperties = new Properties();
        configurationProperties.setProperty(SERVER_DIRECTORY_PATH_PROPERTY, this.serverDirectoryPath);
        configurationProperties.setProperty(USERNAME_PROPERTY, this.username);
        configurationProperties.setProperty(PASSWORD_PROPERTY, this.password);
        configurationProperties.setProperty(STEAMCMD_PATH, this.steamCmdPath);
        configurationProperties.setProperty(SERVER_PORT, String.valueOf(this.serverPort));
        configurationProperties.setProperty(STEAM_API_KEY, this.steamApiKey);
        configurationProperties.setProperty(STEAMCMD_USERNAME, this.steamCmdUsername);
        configurationProperties.setProperty(STEAMCMD_PASSWORD, this.steamCmdPassword);
        configurationProperties.setProperty(FILE_SCANNER_INSTALLATION_ENABLED_PROPERTY, String.valueOf(this.fileScannerInstallationEnabled));
        configurationProperties.setProperty(FILE_SCANNER_DELETION_ENABLED_PROPERTY, String.valueOf(this.fileScannerDeletionEnabled));
        configurationProperties.setProperty(DIFFICULTY_PROFILE_INSTALLATION_SCANNER_ENABLED, String.valueOf(this.difficultyProfileInstallationScannerEnabled));
        configurationProperties.setProperty(MOD_SETTINGS_INSTLLATION_SCANNER_ENABLED, String.valueOf(this.modSettingsInstallationScannerEnabled));
        configurationProperties.setProperty(DISCORD_WEBHOOK_URL, this.discordWebhookUrl);
        configurationProperties.setProperty(DISCORD_WEBHOOK_ENABLED, String.valueOf(this.discordWebhookEnabled));
        configurationProperties.setProperty(VANILLA_MISSIONS_IMPORTER, String.valueOf(this.vanillaMissionsImporter));
        return configurationProperties;
    }
}
