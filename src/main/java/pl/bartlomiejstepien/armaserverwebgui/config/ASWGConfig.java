package pl.bartlomiejstepien.armaserverwebgui.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Component
public class ASWGConfig
{
    private static final Path ASWG_CONFIGURATION_FILE_PATH = Paths.get("aswg-config.properties");

    private static final String SERVER_DIRECTORY_PATH_PROPERTY = "server-directory-path";
    private static final String USERNAME_PROPERTY = "username";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String SERVER_COMMAND_LINE_PARAMETERS_PROPERTY = "server-command-line-parameters";

    private Properties configurationProperties = new Properties();

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationRead() throws IOException
    {
        // Create configuration file on startup
        createConfigFileIfNotExists();
        loadProperties();
    }

    private void createConfigFileIfNotExists() throws IOException
    {
        if (Files.notExists(ASWG_CONFIGURATION_FILE_PATH))
        {
            Files.createFile(ASWG_CONFIGURATION_FILE_PATH);
            this.configurationProperties = new Properties();
            configurationProperties.setProperty(SERVER_DIRECTORY_PATH_PROPERTY, "");
            configurationProperties.setProperty(USERNAME_PROPERTY, "user");
            configurationProperties.setProperty(PASSWORD_PROPERTY, "changeme");
            configurationProperties.setProperty(SERVER_COMMAND_LINE_PARAMETERS_PROPERTY, "");

            saveProperties();
        }
    }

    private void loadProperties() throws IOException
    {
        try(InputStream inputStream = new FileInputStream(ASWG_CONFIGURATION_FILE_PATH.toFile());
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
        {
            configurationProperties.load(bufferedReader);
        }
    }

    public String getUsername()
    {
        return this.configurationProperties.getProperty(USERNAME_PROPERTY, "user");
    }

    public String getPassword()
    {
        return this.configurationProperties.getProperty(PASSWORD_PROPERTY, "changeme");
    }

    public String getServerDirectory()
    {
        return this.configurationProperties.getProperty(SERVER_DIRECTORY_PATH_PROPERTY, "");
    }

    public void setServerDirectory(String path)
    {
        this.configurationProperties.setProperty(SERVER_DIRECTORY_PATH_PROPERTY, path);
        saveProperties();
    }

    public String getServerCommandLineParameters()
    {
        return this.configurationProperties.getProperty(SERVER_COMMAND_LINE_PARAMETERS_PROPERTY, "");
    }

    private void saveProperties()
    {
        try(FileWriter fileWriter = new FileWriter(ASWG_CONFIGURATION_FILE_PATH.toFile(), StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter))
        {
            configurationProperties.store(bufferedWriter, "ASWG Configuration File");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
