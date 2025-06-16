package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(
        status = HttpStatus.INTERNAL_SERVER_ERROR,
        code = ApiExceptionCode.MOD_ID_CANNOT_BE_ZERO,
        messageKey = "aswg.mod.id-cannot-be-zero")
public class ModIdCannotBeZeroException extends RuntimeException
{

}
