package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiErrorResponseResolver;
import pl.bartlomiejstepien.armaserverwebgui.web.response.RestErrorResponse;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class ErrorRestController
{
    private final ApiErrorResponseResolver apiErrorResponseResolver;

    /**
     * Global REST exception handler
     *
     * @param runtimeException the runtime exception
     * @return the {@link ResponseEntity}
     */
    @ExceptionHandler(value = Throwable.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestErrorResponse> handleException(Throwable runtimeException)
    {
        log.error(runtimeException.getMessage(), runtimeException);
        RestErrorResponse restErrorResponse = apiErrorResponseResolver.resolve(runtimeException);
        return ResponseEntity.status(restErrorResponse.getStatus()).body(restErrorResponse);
    }
}
