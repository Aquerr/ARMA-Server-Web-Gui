package pl.bartlomiejstepien.armaserverwebgui.web.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;

@ApiException(status = HttpStatus.BAD_REQUEST, messageKey = "aswg.file.type-not-allowed")
public class NotAllowedFileTypeException extends RuntimeException
{
    public NotAllowedFileTypeException(String message)
    {
        super(message);
    }
}
