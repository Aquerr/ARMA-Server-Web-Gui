package pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception;

public class CouldNotInstallWorkshopModException extends RuntimeException
{
    public CouldNotInstallWorkshopModException(String message)
    {
        super(message);
    }

    public CouldNotInstallWorkshopModException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
