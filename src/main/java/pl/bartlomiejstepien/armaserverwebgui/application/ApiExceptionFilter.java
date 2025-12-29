package pl.bartlomiejstepien.armaserverwebgui.application;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.bartlomiejstepien.armaserverwebgui.web.response.RestErrorResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE + 3)
@Component
@Slf4j
@RequiredArgsConstructor
public class ApiExceptionFilter extends OncePerRequestFilter
{
    private final ApiErrorResponseResolver apiErrorResponseResolver;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        try
        {
            filterChain.doFilter(request, response);
        }
        catch (Exception exception)
        {
            if (!request.getRequestURI().startsWith("/api"))
                throw exception;

            prepareAndSendError(response, exception);
        }
    }

    private void prepareAndSendError(HttpServletResponse response, Exception exception) throws IOException
    {
        log.error(exception.getMessage(), exception);
        RestErrorResponse restErrorResponse = apiErrorResponseResolver.resolve(exception);
        response.setStatus(restErrorResponse.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(restErrorResponse));
    }
}
