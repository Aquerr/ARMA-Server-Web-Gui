package pl.bartlomiejstepien.armaserverwebgui.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import pl.bartlomiejstepien.armaserverwebgui.web.response.RestErrorResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;


@Component
@Order(-1)
@AllArgsConstructor
@Slf4j
public class RestApiExceptionFilter implements WebFilter
{
    private final ServerCodecConfigurer serverCodecConfigurer;

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain)
    {
        return Mono.just(exchange)
                .flatMap(chain::filter)
                .log()
                .doOnError(throwable -> log.error("WTF", throwable))
                .onErrorResume(throwable -> handleThrowable(exchange, throwable))
                .then();
    }

    private Mono<? extends Void> handleThrowable(ServerWebExchange exchange, Throwable throwable)
    {
        log.error("Handling server error", throwable);
        return ServerResponse.status(HttpStatus.I_AM_A_TEAPOT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(RestErrorResponse.of("1337L", HttpStatus.INTERNAL_SERVER_ERROR.value())))
                .flatMap(serverResponse -> serverResponse.writeTo(exchange, new ServerResponse.Context()
                {
                    @NonNull
                    @Override
                    public List<HttpMessageWriter<?>> messageWriters()
                    {
                        // serverCodecConfigurer is an autowired field.
                        return serverCodecConfigurer.getWriters();
                    }

                    @NonNull
                    @Override
                    public List<ViewResolver> viewResolvers()
                    {
                        return Collections.emptyList();
                    }
                }));
    }
}
