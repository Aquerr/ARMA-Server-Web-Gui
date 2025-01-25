package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;

@ApiException(status = HttpStatus.CONFLICT, messageKey = "aswg.mod.already-exists")
public class ModFileAlreadyExistsException extends RuntimeException
{

}
