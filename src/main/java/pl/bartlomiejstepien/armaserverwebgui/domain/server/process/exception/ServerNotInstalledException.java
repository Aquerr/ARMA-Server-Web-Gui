package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;

@ApiException(status = HttpStatus.INTERNAL_SERVER_ERROR, messageKey = "aswg.server.not-installed")
public class ServerNotInstalledException extends RuntimeException
{
    public ServerNotInstalledException()
    {
    }

    public ServerNotInstalledException(String message)
    {
        super(message);
    }
}
