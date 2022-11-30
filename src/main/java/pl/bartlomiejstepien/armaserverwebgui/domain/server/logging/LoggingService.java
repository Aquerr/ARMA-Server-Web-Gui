package pl.bartlomiejstepien.armaserverwebgui.domain.server.logging;

import pl.bartlomiejstepien.armaserverwebgui.domain.model.LoggingProperties;

public interface LoggingService
{
    LoggingProperties getLoggingProperties();

    void saveLoggingProperties(LoggingProperties loggingProperties);
}
