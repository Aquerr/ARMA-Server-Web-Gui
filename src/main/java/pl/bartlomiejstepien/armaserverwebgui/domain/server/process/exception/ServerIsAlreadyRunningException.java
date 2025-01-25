package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;

@ApiException(status = HttpStatus.BAD_REQUEST, messageKey = "aswg.server.already-running")
public class ServerIsAlreadyRunningException extends RuntimeException
{
    public ServerIsAlreadyRunningException(String message)
    {
        super(message);
    }
}
