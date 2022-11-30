package pl.bartlomiejstepien.armaserverwebgui.application.config.security;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(-2)
@Component
public class FrontEndRedirectWebFilter implements WebFilter
{

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain)
    {
        String path = exchange.getRequest().getURI().getPath();
        if (isNotApiRequest(path)) {
            return chain.filter(
                    exchange.mutate().request(exchange.getRequest().mutate().path("/index.html").build())
                            .build());
        }
        return chain.filter(exchange);
    }

    private boolean isNotApiRequest(String path)
    {
        return !path.startsWith("/api") && path.matches("[^\\\\.]*");
    }
}
