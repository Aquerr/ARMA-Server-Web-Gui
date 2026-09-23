package pl.bartlomiejstepien.armaserverwebgui.application.file.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.BAD_REQUEST, code = ApiExceptionCode.COULD_NOT_PARSE_FILE, messageKey = "aswg.file.could-not-parse-file")
public class CouldNotParseFileException extends RuntimeException
{
    public CouldNotParseFileException(Exception exception)
    {
        super(exception);
    }
}
