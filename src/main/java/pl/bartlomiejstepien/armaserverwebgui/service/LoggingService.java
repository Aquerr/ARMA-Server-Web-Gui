package pl.bartlomiejstepien.armaserverwebgui.service;

import pl.bartlomiejstepien.armaserverwebgui.model.LoggingProperties;

public interface LoggingService
{
    LoggingProperties getLoggingSectionData();

    void saveLoggingSectionData(LoggingProperties loggingProperties);
}
