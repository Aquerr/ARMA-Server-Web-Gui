package pl.bartlomiejstepien.armaserverwebgui.config.security.filter;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import pl.bartlomiejstepien.armaserverwebgui.util.JwtTokenUtil;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter extends AuthenticationWebFilter
{
    private static final String BEARER = "Bearer ";

//    private final JwtTokenUtil jwtTokenUtil;

    public JwtFilter(ReactiveAuthenticationManager authenticationManager)
    {
        super(authenticationManager);
    }

//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain)
//    {
//        if ()
//        {
//
//            chain.filter(exchange);
//        }
//        chain.filter(exchange);
//    }
//
//    private String getTokenFromHeader(ServerWebExchange serverWebExchange)
//    {
//        String authorizationHeader = serverWebExchange.getRequest().getHeaders(HttpHeaders.AUTHORIZATION);
//        if (authorizationHeader.startsWith(BEARER))
//        {
//            return
//        }
//
//    }
}
