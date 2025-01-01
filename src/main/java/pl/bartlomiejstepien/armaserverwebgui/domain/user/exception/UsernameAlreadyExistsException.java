package pl.bartlomiejstepien.armaserverwebgui.domain.user.exception;

public class UsernameAlreadyExistsException extends RuntimeException
{
    public UsernameAlreadyExistsException(String message)
    {
        super(message);
    }
}
