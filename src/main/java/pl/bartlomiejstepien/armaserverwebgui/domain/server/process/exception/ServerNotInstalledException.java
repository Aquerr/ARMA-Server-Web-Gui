package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.INTERNAL_SERVER_ERROR, code = ApiExceptionCode.SERVER_NOT_INSTALLED, messageKey = "aswg.server.not-installed")
public class ServerNotInstalledException extends RuntimeException
{
    public ServerNotInstalledException(String message)
    {
        super(message);
    }
}
