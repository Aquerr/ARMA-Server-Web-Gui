package pl.bartlomiejstepien.armaserverwebgui.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.i18n.MessageService;
import pl.bartlomiejstepien.armaserverwebgui.web.response.RestErrorResponse;

@Component
@RequiredArgsConstructor
public class ApiErrorResponseResolver
{
    private static final String DEFAULT_ERROR_CODE = "SERVER_ERROR";

    private final MessageService messageService;

    public RestErrorResponse resolve(Throwable throwable)
    {
        if (throwable.getClass().isAnnotationPresent(ApiException.class))
        {
            ApiException apiException = throwable.getClass().getAnnotation(ApiException.class);
            return resolveRestErrorResponse(apiException);
        }
        else
        {
            if (throwable instanceof AccessDeniedException)
            {
                return RestErrorResponse.of(HttpStatus.FORBIDDEN.getReasonPhrase(), ApiExceptionCode.ACCESS_DENIED.name(), HttpStatus.FORBIDDEN.value());
            }

            return RestErrorResponse.of(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    DEFAULT_ERROR_CODE,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private RestErrorResponse resolveRestErrorResponse(ApiException apiException)
    {
        return RestErrorResponse.of(
                messageService.resolveExceptionMessage(apiException.messageKey()),
                apiException.code().name(),
                apiException.status().value()
        );
    }
}
