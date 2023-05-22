package pl.bartlomiejstepien.armaserverwebgui.domain.server.logging;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.model.LoggingProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;

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
