package pl.bartlomiejstepien.armaserverwebgui.application.security.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.BAD_REQUEST, code = ApiExceptionCode.BAD_CREDENTIALS, messageKey = "aswg.security.bad-credentials")
public class BadCredentialsException extends RuntimeException
{

}
