package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import lombok.Value;

@Value
public class WorkshopModInstallationStatus
{
    long fileId;
    int status;
}
