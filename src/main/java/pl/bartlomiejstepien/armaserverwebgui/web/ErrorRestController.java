package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bartlomiejstepien.armaserverwebgui.web.response.RestErrorResponse;

@RestControllerAdvice
@Slf4j
public class ErrorRestController
{
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse handleException(RuntimeException runtimeException)
    {
        log.error(runtimeException.getMessage(), runtimeException);
        return RestErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
