package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.BAD_REQUEST, code = ApiExceptionCode.SERVER_ALREADY_RUNNING, messageKey = "aswg.server.already-running")
public class ServerIsAlreadyRunningException extends RuntimeException
{
    public ServerIsAlreadyRunningException(String message)
    {
        super(message);
    }
}
