package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Value;

@Value
public class WorkshopModInstallSteamTask implements SteamTask
{
    long fileId;
    String title;

    @Override
    public Type getType()
    {
        return Type.WORKSHOP_DOWNLOAD;
    }
}
