package pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception;

import java.util.List;
import java.util.Set;

public class CouldNotBatchDownloadWorkshopModsException extends RetryableException
{
    private final Set<Long> failedWorkshopModIds;

    public CouldNotBatchDownloadWorkshopModsException(Set<Long> failedWorkshopModIds, String message)
    {
        super(message);
        this.failedWorkshopModIds = Set.copyOf(failedWorkshopModIds);
    }

    public CouldNotBatchDownloadWorkshopModsException(List<Long> failedWorkshopModIds, String message, Throwable cause)
    {
        super(message, cause);
        this.failedWorkshopModIds = Set.copyOf(failedWorkshopModIds);
    }

    public Set<Long> getFailedWorkshopModIds()
    {
        return failedWorkshopModIds;
    }
}
