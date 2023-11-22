package pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception;

public class CouldNotInstallWorkshopModException extends RetryableException
{
    public CouldNotInstallWorkshopModException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
