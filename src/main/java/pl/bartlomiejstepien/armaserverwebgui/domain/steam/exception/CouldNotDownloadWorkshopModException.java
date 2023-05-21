package pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception;

public class CouldNotDownloadWorkshopModException extends Exception
{
    public CouldNotDownloadWorkshopModException(String message)
    {
        super(message);
    }

    public CouldNotDownloadWorkshopModException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
