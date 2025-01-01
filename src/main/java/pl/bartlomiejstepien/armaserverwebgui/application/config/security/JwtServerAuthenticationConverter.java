package pl.bartlomiejstepien.armaserverwebgui.application.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter
{
    private final JwtService jwtService;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange)
    {
        return Mono.justOrEmpty(exchange)
                .mapNotNull(jwtService::extractJwt)
                .filter(Objects::nonNull)
                .map(jwt -> UsernamePasswordAuthenticationToken.unauthenticated(jwt, jwt));
    }
}
