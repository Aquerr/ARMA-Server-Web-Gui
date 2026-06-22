package pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.exception;

public class CouldNotReadDifficultyProfileException extends RuntimeException
{
    public CouldNotReadDifficultyProfileException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
