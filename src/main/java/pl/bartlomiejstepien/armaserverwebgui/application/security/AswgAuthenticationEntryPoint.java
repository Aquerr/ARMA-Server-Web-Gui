package pl.bartlomiejstepien.armaserverwebgui.application.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.security.exception.AuthTokenRequiredException;

@Component
public class AswgAuthenticationEntryPoint implements AuthenticationEntryPoint
{
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException
    {
        if (authException instanceof AuthenticationCredentialsNotFoundException)
        {
            throw new AuthTokenRequiredException("Auth token required for protected endpoint: " + request.getRequestURI());
        }

        throw new ServletException("Authentication failed", authException);
    }
}
