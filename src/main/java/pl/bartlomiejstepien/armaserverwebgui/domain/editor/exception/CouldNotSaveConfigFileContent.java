package pl.bartlomiejstepien.armaserverwebgui.domain.editor.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.BAD_REQUEST, code = ApiExceptionCode.COULD_NOT_SAVE_CONFIG_FILE, messageKey = "aswg.file.could-not-save-config-file")
public class CouldNotSaveConfigFileContent extends RuntimeException
{
}
