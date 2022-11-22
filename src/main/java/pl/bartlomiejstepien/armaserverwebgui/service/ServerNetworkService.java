package pl.bartlomiejstepien.armaserverwebgui.service;

import pl.bartlomiejstepien.armaserverwebgui.model.NetworkProperties;

public interface ServerNetworkService
{
    NetworkProperties getNetworkProperties();

    void saveNetworkProperties(NetworkProperties networkProperties);
}
