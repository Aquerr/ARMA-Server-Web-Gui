package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import lombok.Value;

@Value
public class WorkshopModInstallationRequest
{
    long fileId;
    String title;
}
