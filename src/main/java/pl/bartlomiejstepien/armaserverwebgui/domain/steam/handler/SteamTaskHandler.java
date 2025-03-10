package pl.bartlomiejstepien.armaserverwebgui.domain.steam.handler;

import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamTask;

public interface SteamTaskHandler
{
    void handle(SteamTask steamTask);
}
