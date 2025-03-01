package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.CONFLICT, code = ApiExceptionCode.MOD_ALREADY_EXISTS, messageKey = "aswg.mod.already-exists")
public class ModFileAlreadyExistsException extends RuntimeException
{

}
