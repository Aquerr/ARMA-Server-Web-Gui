package pl.bartlomiejstepien.armaserverwebgui.config;

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
    private static final String SERVER_COMMAND_LINE_PARAMETERS_PROPERTY = "aswg.server-command-line-parameters";

//    private Properties configurationProperties = new Properties();

    @Value("${aswg.username}")
    private String username;
    @Value("${aswg.password}")
    private String password;
    @Value("${aswg.server-directory-path}")
    private String serverDirectoryPath;
    @Value("${aswg.server-command-line-parameters}")
    private String serverCommandLineParameters;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationRead() throws IOException
    {
        // Create configuration file on startup
        createConfigFileIfNotExists();
//        loadProperties();
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
            configurationProperties.setProperty(SERVER_COMMAND_LINE_PARAMETERS_PROPERTY, this.serverCommandLineParameters);

            saveProperties();
        }
    }

//    private void loadProperties() throws IOException
//    {
//        try(InputStream inputStream = new FileInputStream(ASWG_CONFIGURATION_FILE_PATH.toFile());
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
//        {
//            configurationProperties.load(bufferedReader);
//        }
//    }

//    public String getUsername()
//    {
//        return this.configurationProperties.getProperty(USERNAME_PROPERTY, "user");
//    }
//
//    public String getPassword()
//    {
//        return this.configurationProperties.getProperty(PASSWORD_PROPERTY, "changeme");
//    }
//
//    public String getServerDirectory()
//    {
//        return this.configurationProperties.getProperty(SERVER_DIRECTORY_PATH_PROPERTY, "");
//    }
//
//    public void setServerDirectory(String path)
//    {
//        this.configurationProperties.setProperty(SERVER_DIRECTORY_PATH_PROPERTY, path);
//        saveProperties();
//    }
//
//    public String getServerCommandLineParameters()
//    {
//        return this.configurationProperties.getProperty(SERVER_COMMAND_LINE_PARAMETERS_PROPERTY, "");
//    }


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

    public String getServerCommandLineParameters()
    {
        return serverCommandLineParameters;
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
            configurationProperties.setProperty(SERVER_COMMAND_LINE_PARAMETERS_PROPERTY, this.serverCommandLineParameters);
            configurationProperties.store(bufferedWriter, "ASWG Configuration File");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
