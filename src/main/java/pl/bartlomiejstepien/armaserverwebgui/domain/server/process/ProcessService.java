package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ServerProcessStatus;

import java.util.List;

public interface ProcessService
{
    ServerProcessStatus getProcessStatus();

    void startServer(boolean performUpdate);

    void stopServer();

    List<String> getLatestServerLogs();
}
