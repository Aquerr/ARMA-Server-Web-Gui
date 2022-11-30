package pl.bartlomiejstepien.armaserverwebgui.domain.server.network;

import pl.bartlomiejstepien.armaserverwebgui.domain.model.NetworkProperties;

public interface ServerNetworkService
{
    NetworkProperties getNetworkProperties();

    void saveNetworkProperties(NetworkProperties networkProperties);
}
