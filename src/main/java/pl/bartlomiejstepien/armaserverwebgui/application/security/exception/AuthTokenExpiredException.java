package pl.bartlomiejstepien.armaserverwebgui.application.security.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.UNAUTHORIZED, code = ApiExceptionCode.AUTH_TOKEN_EXPIRED, messageKey = "aswg.security.auth-token-expired")
public class AuthTokenExpiredException extends RuntimeException
{

}
