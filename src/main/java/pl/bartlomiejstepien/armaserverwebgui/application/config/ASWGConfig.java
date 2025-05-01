package pl.bartlomiejstepien.armaserverwebgui.application.config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@RequiredArgsConstructor
public class ASWGConfig
{
    private static final Path ASWG_CONFIGURATION_FILE_PATH = Paths.get("aswg-config.properties");

    private static final String SERVER_DIRECTORY_PATH_PROPERTY = "aswg.server-directory-path";
    private static final String MODS_DIRECTORY_PATH_PROPERTY = "aswg.mods-directory-path";

    private static final String USERNAME_PROPERTY = "aswg.default-user.username";
    private static final String PASSWORD_PROPERTY = "aswg.default-user.password";
    private static final String RESET_DEFAULT_USER = "aswg.default-user.reset";
    private static final String STEAMCMD_PATH = "aswg.steamcmd.path";
    private static final String STEAMCMD_USERNAME = "aswg.steamcmd.username";
    private static final String STEAMCMD_PASSWORD = "aswg.steamcmd.password";
    private static final String STEAM_API_KEY = "aswg.steam.web-api-token";

    private static final String SERVER_PORT = "aswg.server-port";

    private static final String FILE_SCANNER_INSTALLATION_ENABLED_PROPERTY = "aswg.job.mods-scanner.installation.enabled";
    private static final String FILE_SCANNER_DELETION_ENABLED_PROPERTY = "aswg.job.mods-scanner.deletion.enabled";
    private static final String DIFFICULTY_PROFILE_INSTALLATION_SCANNER_ENABLED = "aswg.job.difficulty-scanner.installation.enabled";
    private static final String MOD_SETTINGS_INSTLLATION_SCANNER_ENABLED = "aswg.job.mod-settings-scanner.installation.enabled";
    private static final String VANILLA_MISSIONS_IMPORTER = "aswg.vanilla-missions-importer.enabled";

    private static final String JOB_MOD_UPDATE_ENABLED_PROPERTY = "aswg.job.mod-update.enabled";
    private static final String JOB_MOD_UPDATE_CRON_PROPERTY = "aswg.job.mod-update.cron";

    // Discord
    private static final String DISCORD_WEBHOOK_ENABLED = "aswg.discord.webhook.enabled";
    private static final String DISCORD_WEBHOOK_URL = "aswg.discord.webhook.url";
    private static final String DISCORD_MESSAGE_SERVER_STARTING = "aswg.discord.message.server-starting";
    private static final String DISCORD_MESSAGE_SERVER_START = "aswg.discord.message.server-start";
    private static final String DISCORD_MESSAGE_SERVER_STOP = "aswg.discord.message.server-stop";
    private static final String DISCORD_MESSAGE_SERVER_UPDATE = "aswg.discord.message.server-update";

    private static final String SERVER_BRANCH = "aswg.server.branch";

    @Value("${aswg.default-user.username:}")
    private String username;
    @Value("${aswg.default-user.password:}")
    private String password;
    @Value("${aswg.default-user.reset:false}")
    private boolean resetDefaultUser;
    @Value("${aswg.server-port:2302}")
    private int serverPort;
    @Value("${aswg.server-directory-path:}")
    private String serverDirectoryPath;
    @Value("${aswg.mods-directory-path:}")
    private String modsDirectoryPath;

    @Value("${aswg.steamcmd.path:}")
    private String steamCmdPath;
    @Value("${aswg.steamcmd.username:}")
    private String steamCmdUsername;
    @Value("${aswg.steamcmd.password:}")
    private String steamCmdPassword;
    @Value("${aswg.steam.web-api-token:}")
    private String steamApiKey;

    @Value("${aswg.job.mods-scanner.installation.enabled:true}")
    private boolean modsScannerInstallationEnabled;
    @Value("${aswg.job.mods-scanner.installation.enabled:false}")
    private boolean modsScannerDeletionEnabled;

    @Value("${aswg.job.difficulty-scanner.installation.enabled:true}")
    private boolean difficultyProfileInstallationScannerEnabled;
    @Value("${aswg.job.mod-settings-scanner.installation.enabled:true}")
    private boolean modSettingsInstallationScannerEnabled;
    @Value("${aswg.vanilla-missions-importer.enabled:false}")
    private boolean vanillaMissionsImporter;

    @Value("${" + JOB_MOD_UPDATE_ENABLED_PROPERTY + ":true}")
    private boolean jobModUpdateEnabled;

    @Value("${" + JOB_MOD_UPDATE_CRON_PROPERTY + ":0 0 1 * * *}")
    private String jobModUpdateCron;

    @Value("${aswg.server.branch:public}")
    private String serverBranch;

    private final DiscordProperties discordProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void onEnvironmentPreparedEvent() throws IOException
    {
        // Create configuration file on startup
        createConfigFileIfNotExists();
    }

    public void saveToFile()
    {
        try (FileWriter fileWriter = new FileWriter(ASWG_CONFIGURATION_FILE_PATH.toFile(), StandardCharsets.UTF_8);
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
            saveToFile();
        }
    }

    private Properties prepareProperties()
    {
        Properties configurationProperties = new Properties();
        configurationProperties.setProperty(SERVER_DIRECTORY_PATH_PROPERTY, this.serverDirectoryPath);
        configurationProperties.setProperty(MODS_DIRECTORY_PATH_PROPERTY, this.modsDirectoryPath);
        configurationProperties.setProperty(USERNAME_PROPERTY, this.username);
        configurationProperties.setProperty(PASSWORD_PROPERTY, this.password);
        configurationProperties.setProperty(RESET_DEFAULT_USER, String.valueOf(this.resetDefaultUser));
        configurationProperties.setProperty(STEAMCMD_PATH, this.steamCmdPath);
        configurationProperties.setProperty(SERVER_PORT, String.valueOf(this.serverPort));
        configurationProperties.setProperty(STEAM_API_KEY, this.steamApiKey);
        configurationProperties.setProperty(STEAMCMD_USERNAME, this.steamCmdUsername);
        configurationProperties.setProperty(STEAMCMD_PASSWORD, this.steamCmdPassword);
        configurationProperties.setProperty(FILE_SCANNER_INSTALLATION_ENABLED_PROPERTY, String.valueOf(this.modsScannerInstallationEnabled));
        configurationProperties.setProperty(FILE_SCANNER_DELETION_ENABLED_PROPERTY, String.valueOf(this.modsScannerDeletionEnabled));
        configurationProperties.setProperty(DIFFICULTY_PROFILE_INSTALLATION_SCANNER_ENABLED, String.valueOf(this.difficultyProfileInstallationScannerEnabled));
        configurationProperties.setProperty(MOD_SETTINGS_INSTLLATION_SCANNER_ENABLED, String.valueOf(this.modSettingsInstallationScannerEnabled));

        configurationProperties.setProperty(JOB_MOD_UPDATE_ENABLED_PROPERTY, String.valueOf(this.jobModUpdateEnabled));
        configurationProperties.setProperty(JOB_MOD_UPDATE_CRON_PROPERTY, this.jobModUpdateCron);

        configurationProperties.setProperty(DISCORD_WEBHOOK_URL, this.discordProperties.getWebhookUrl());
        configurationProperties.setProperty(DISCORD_WEBHOOK_ENABLED, String.valueOf(this.discordProperties.isEnabled()));
        configurationProperties.setProperty(DISCORD_MESSAGE_SERVER_STARTING, this.discordProperties.getMessageServerStarting());
        configurationProperties.setProperty(DISCORD_MESSAGE_SERVER_START, this.discordProperties.getMessageServerStart());
        configurationProperties.setProperty(DISCORD_MESSAGE_SERVER_STOP, this.discordProperties.getMessageServerStop());
        configurationProperties.setProperty(DISCORD_MESSAGE_SERVER_UPDATE, this.discordProperties.getMessageServerUpdate());

        configurationProperties.setProperty(VANILLA_MISSIONS_IMPORTER, String.valueOf(this.vanillaMissionsImporter));
        configurationProperties.setProperty(SERVER_BRANCH, String.valueOf(this.serverBranch));
        return configurationProperties;
    }

    @Data
    @Component
    public static class DiscordProperties
    {
        @Value("${aswg.discord.webhook.enabled:false}")
        private boolean enabled;
        @Value("${aswg.discord.webhook.url:}")
        private String webhookUrl;
        @Value("${aswg.discord.message.server-starting:}")
        private String messageServerStarting;
        @Value("${aswg.discord.message.server-start:}")
        private String messageServerStart;
        @Value("${aswg.discord.message.server-stop:}")
        private String messageServerStop;
        @Value("${aswg.discord.message.server-update:}")
        private String messageServerUpdate;
    }
}
