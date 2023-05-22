package pl.bartlomiejstepien.armaserverwebgui.domain.server.logging.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoggingProperties
{
    private String logFile;
}
