package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import lombok.Value;

@Value
public class WorkshopModInstallationRequest
{
    long fileId;
    String title;
    int installAttemptCount;

    public WorkshopModInstallationRequest(long fileId, String title)
    {
        this.fileId = fileId;
        this.title = title;
        this.installAttemptCount = 1;
    }

    public WorkshopModInstallationRequest(long fileId, String title, int downloadAttemptCount)
    {
        this.fileId = fileId;
        this.title = title;
        this.installAttemptCount = downloadAttemptCount;
    }
}
