package pl.bartlomiejstepien.armaserverwebgui.service;

import pl.bartlomiejstepien.armaserverwebgui.model.ServerStatus;

public interface StatusService
{
    ServerStatus getServerStatus();

    boolean startServer();

    boolean stopServer();
}
