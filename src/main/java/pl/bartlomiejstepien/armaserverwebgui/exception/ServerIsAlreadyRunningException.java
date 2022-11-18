package pl.bartlomiejstepien.armaserverwebgui.exception;

public class ServerIsAlreadyRunningException extends RuntimeException
{
    public ServerIsAlreadyRunningException(String message)
    {
        super(message);
    }
}
