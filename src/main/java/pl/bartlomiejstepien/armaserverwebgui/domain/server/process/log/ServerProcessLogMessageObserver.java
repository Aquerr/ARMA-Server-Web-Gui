package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.log;

public interface ServerProcessLogMessageObserver
{
    void handleServerLogMessage(String log);
}
