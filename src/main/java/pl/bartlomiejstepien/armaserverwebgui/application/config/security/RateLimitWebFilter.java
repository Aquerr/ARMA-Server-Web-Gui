package pl.bartlomiejstepien.armaserverwebgui.application.config.security;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

//TODO: Implement it...
@Order(-4)
@Component
public class RateLimitWebFilter implements WebFilter
{
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain)
    {
        return chain.filter(exchange);
    }
}
