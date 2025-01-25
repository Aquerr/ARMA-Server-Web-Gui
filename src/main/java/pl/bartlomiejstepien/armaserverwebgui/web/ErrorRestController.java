package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.i18n.MessageService;
import pl.bartlomiejstepien.armaserverwebgui.web.response.RestErrorResponse;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class ErrorRestController
{
    private final MessageService messageService;

    /**
     * Global REST exception handler
     * @param runtimeException the runtime exception
     * @return the {@link ResponseEntity}
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<RestErrorResponse> handleException(Throwable runtimeException)
    {
        log.error(runtimeException.getMessage(), runtimeException);

        if (runtimeException.getClass().isAnnotationPresent(ApiException.class)) {
            ApiException apiException = runtimeException.getClass().getAnnotation(ApiException.class);
            return resolveRestErrorResponse(apiException);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(RestErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    private ResponseEntity<RestErrorResponse> resolveRestErrorResponse(ApiException apiException)
    {
        return ResponseEntity.status(apiException.status().value())
                .body(RestErrorResponse.of(messageService.resolveExceptionMessage(apiException.messageKey()), apiException.status().value()));
    }
}
