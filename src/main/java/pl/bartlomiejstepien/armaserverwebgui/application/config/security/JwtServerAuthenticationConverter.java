package pl.bartlomiejstepien.armaserverwebgui.application.config.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter
{
    private static final String BEARER = "Bearer ";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange)
    {
        return Mono.justOrEmpty(exchange)
                .flatMap(serverWebExchange -> Mono.justOrEmpty(serverWebExchange.getRequest().getHeaders().getFirst("Authorization")))
                .filter(Objects::nonNull)
                .filter(jwt -> jwt.startsWith(BEARER))
                .map(jwt -> jwt.substring(BEARER.length()))
                .map(jwt -> new UsernamePasswordAuthenticationToken(jwt, jwt));
    }
}
