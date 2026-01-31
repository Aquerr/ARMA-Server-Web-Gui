package pl.bartlomiejstepien.armaserverwebgui.application.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.auth.AswgAuthentication;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter
{
    private final JwtService jwtService;

    @Override
    public AswgAuthentication convert(HttpServletRequest request)
    {
        return Optional.of(request)
                .map(jwtService::extractJwt)
                .map(AswgAuthentication::unauthenticated)
                .orElse(null);
    }
}
