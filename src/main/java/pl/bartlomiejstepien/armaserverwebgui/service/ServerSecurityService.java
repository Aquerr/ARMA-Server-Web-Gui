package pl.bartlomiejstepien.armaserverwebgui.service;

import pl.bartlomiejstepien.armaserverwebgui.model.ServerSecurity;

public interface ServerSecurityService
{
    ServerSecurity getServerSecurity();

    void saveServerSecurity(ServerSecurity serverSecurity);
}
