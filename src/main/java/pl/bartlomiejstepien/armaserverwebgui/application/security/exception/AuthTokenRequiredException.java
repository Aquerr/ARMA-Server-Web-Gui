package pl.bartlomiejstepien.armaserverwebgui.application.security.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.FORBIDDEN, code = ApiExceptionCode.AUTH_TOKEN_MISSING, messageKey = "aswg.security.auth-token-missing")
public class AuthTokenRequiredException extends RuntimeException
{
    public AuthTokenRequiredException(String message)
    {
        super(message);
    }
}
