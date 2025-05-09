package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Value;

import java.util.UUID;

@Value
public class QueuedSteamTask
{
    UUID id;
    SteamTask steamTask;
    int retryCount;
}
