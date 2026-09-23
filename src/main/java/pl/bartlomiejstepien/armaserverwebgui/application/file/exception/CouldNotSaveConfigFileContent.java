package pl.bartlomiejstepien.armaserverwebgui.application.file.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.BAD_REQUEST, code = ApiExceptionCode.COULD_NOT_SAVE_FILE, messageKey = "aswg.file.could-not-save-file")
public class CouldNotSaveConfigFileContent extends RuntimeException
{
    public CouldNotSaveConfigFileContent(Exception exception)
    {
        super(exception);
    }
}
