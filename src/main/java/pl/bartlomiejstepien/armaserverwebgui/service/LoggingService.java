package pl.bartlomiejstepien.armaserverwebgui.service;

import pl.bartlomiejstepien.armaserverwebgui.model.LoggingProperties;

public interface LoggingService
{
    LoggingProperties getLoggingProperties();

    void saveLoggingProperties(LoggingProperties loggingProperties);
}
