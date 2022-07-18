package pl.bartlomiejstepien.armaserverwebgui.exception;

public class NotAllowedFileTypeException extends RuntimeException
{
    public NotAllowedFileTypeException(String message)
    {
        super(message);
    }
}
