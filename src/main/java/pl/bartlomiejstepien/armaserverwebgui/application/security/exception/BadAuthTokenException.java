package pl.bartlomiejstepien.armaserverwebgui.application.security.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.BAD_REQUEST, code = ApiExceptionCode.BAD_AUTH_TOKEN, messageKey = "aswg.security.bad-auth-token")
public class BadAuthTokenException extends RuntimeException
{
    public BadAuthTokenException(Throwable cause)
    {
        super(cause);
    }
}
