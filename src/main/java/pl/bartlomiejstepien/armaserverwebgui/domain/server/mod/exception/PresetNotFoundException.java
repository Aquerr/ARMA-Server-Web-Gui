package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.NOT_FOUND, code = ApiExceptionCode.PRESET_NOT_FOUND, messageKey = "aswg.preset.not-found")
public class PresetNotFoundException extends RuntimeException
{

}
