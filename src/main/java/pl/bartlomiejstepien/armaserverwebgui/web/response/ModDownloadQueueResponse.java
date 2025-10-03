package pl.bartlomiejstepien.armaserverwebgui.web.response;

import java.util.List;

public record ModDownloadQueueResponse(List<DownloadingMod> mods)
{
    public record DownloadingMod(long fileId, String title, int installAttemptCount, String issuer)
    {
    }
}
