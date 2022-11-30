package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception;

public class ServerIsAlreadyRunningException extends RuntimeException
{
    public ServerIsAlreadyRunningException(String message)
    {
        super(message);
    }
}
