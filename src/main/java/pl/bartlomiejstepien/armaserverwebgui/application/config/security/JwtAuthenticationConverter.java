package pl.bartlomiejstepien.armaserverwebgui.application.config.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter
{
    private final JwtService jwtService;

    @Override
    public Authentication convert(HttpServletRequest request)
    {
        return Optional.ofNullable(request)
                .map(jwtService::extractJwt)
                .filter(Objects::nonNull)
                .map(jwt -> UsernamePasswordAuthenticationToken.unauthenticated(jwt, jwt))
                .orElse(null);
    }
}
