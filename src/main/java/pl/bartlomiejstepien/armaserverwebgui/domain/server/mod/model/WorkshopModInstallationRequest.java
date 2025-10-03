package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model;

import lombok.Value;

@Value
public class WorkshopModInstallationRequest
{
    long fileId;
    String title;
    int installAttemptCount;
    String issuer;

    public WorkshopModInstallationRequest(long fileId, String title, String issuer)
    {
        this.fileId = fileId;
        this.title = title;
        this.installAttemptCount = 1;
        this.issuer = issuer;
    }

    public WorkshopModInstallationRequest(long fileId, String title, int downloadAttemptCount, String issuer)
    {
        this.fileId = fileId;
        this.title = title;
        this.installAttemptCount = downloadAttemptCount;
        this.issuer = issuer;
    }
}
