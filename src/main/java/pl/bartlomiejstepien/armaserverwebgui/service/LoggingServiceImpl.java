package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.model.LoggingProperties;
import pl.bartlomiejstepien.armaserverwebgui.storage.ServerConfigStorage;

@Service
@AllArgsConstructor
public class LoggingServiceImpl implements LoggingService
{
    private final ServerConfigStorage serverConfigStorage;

    @Override
    public LoggingProperties getLoggingProperties()
    {
        ArmaServerConfig armaServerConfig = this.serverConfigStorage.getServerConfig();
        return LoggingProperties.builder()
                .logFile(armaServerConfig.getLogFile())
                .build();
    }

    @Override
    public void saveLoggingProperties(LoggingProperties loggingProperties)
    {
        ArmaServerConfig armaServerConfig = this.serverConfigStorage.getServerConfig();
        armaServerConfig.setLogFile(loggingProperties.getLogFile());
        this.serverConfigStorage.saveServerConfig(armaServerConfig);
    }
}
