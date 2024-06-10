package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception;

public class ParsingException extends Exception
{
    public ParsingException()
    {
    }

    public ParsingException(String message)
    {
        super(message);
    }

    public ParsingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ParsingException(Throwable cause)
    {
        super(cause);
    }
}
