package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class ServerStatus
{
    Status status;
    String statusText;

    public enum Status
    {
        OFFLINE,
        ONLINE,
        RUNNING_BUT_NOT_DETECTED_BY_STEAM,
        STARTING,
        UPDATING
    }
}
