package pl.bartlomiejstepien.armaserverwebgui.web.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.BAD_REQUEST, code = ApiExceptionCode.NOT_ALLOWED_FILE_TYPE, messageKey = "aswg.file.type-not-allowed")
public class NotAllowedFileTypeException extends RuntimeException
{
    public NotAllowedFileTypeException(String message)
    {
        super(message);
    }
}
