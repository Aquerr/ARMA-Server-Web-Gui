package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(
        status = HttpStatus.CONFLICT,
        code = ApiExceptionCode.MOD_ID_ALREADY_REGISTERED,
        messageKey = "aswg.mod.id-already-registered")
public class ModIdAlreadyRegisteredException extends RuntimeException
{

}
