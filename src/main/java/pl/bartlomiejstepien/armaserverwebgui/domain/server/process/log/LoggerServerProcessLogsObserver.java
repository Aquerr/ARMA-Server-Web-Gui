package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggerServerProcessLogsObserver implements ServerProcessLogMessageObserver
{
    private static final Logger SERVER_LOGGER = LoggerFactory.getLogger("arma-server");

    @Override
    public void handleServerLogMessage(String log)
    {
        SERVER_LOGGER.info(log);
    }
}
