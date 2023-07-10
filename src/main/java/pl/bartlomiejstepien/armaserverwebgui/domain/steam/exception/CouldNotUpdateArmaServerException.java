package pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception;

public class CouldNotUpdateArmaServerException extends Exception
{
    public CouldNotUpdateArmaServerException(String message)
    {
        super(message);
    }

    public CouldNotUpdateArmaServerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
