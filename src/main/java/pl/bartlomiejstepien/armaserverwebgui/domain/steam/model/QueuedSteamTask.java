package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import java.util.UUID;
import lombok.Value;

@Value
public class QueuedSteamTask
{
    UUID id;
    SteamTask steamTask;
    int retryCount;
}
