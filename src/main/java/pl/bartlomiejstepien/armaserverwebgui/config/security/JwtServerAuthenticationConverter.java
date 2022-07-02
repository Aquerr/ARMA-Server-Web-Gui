package pl.bartlomiejstepien.armaserverwebgui.config.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter
{
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange)
    {
        return Mono.justOrEmpty(exchange)
                .flatMap(serverWebExchange -> Mono.justOrEmpty(serverWebExchange.getRequest().getCookies().getFirst("X-Auth")))
                .filter(httpCookie -> httpCookie != null)
                .map(httpCookie -> httpCookie.getValue())
                .map(jwt -> new UsernamePasswordAuthenticationToken(jwt, jwt));
    }
}
