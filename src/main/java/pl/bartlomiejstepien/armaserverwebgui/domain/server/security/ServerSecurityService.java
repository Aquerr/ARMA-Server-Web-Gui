package pl.bartlomiejstepien.armaserverwebgui.domain.server.security;

import pl.bartlomiejstepien.armaserverwebgui.domain.model.ServerSecurity;

public interface ServerSecurityService
{
    ServerSecurity getServerSecurity();

    void saveServerSecurity(ServerSecurity serverSecurity);
}
