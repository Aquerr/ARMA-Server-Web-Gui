package pl.bartlomiejstepien.armaserverwebgui.web.exception;

public class NotAllowedFileTypeException extends RuntimeException
{
    public NotAllowedFileTypeException(String message)
    {
        super(message);
    }
}
