package pl.bartlomiejstepien.armaserverwebgui.domain.server.security;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model.ServerSecurityProperties;

public interface ServerSecurityService
{
    ServerSecurityProperties getServerSecurity();

    void saveServerSecurity(ServerSecurityProperties serverSecurityProperties);
}
