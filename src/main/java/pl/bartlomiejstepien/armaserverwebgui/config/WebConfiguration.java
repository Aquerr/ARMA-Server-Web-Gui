package pl.bartlomiejstepien.armaserverwebgui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.WebFilter;
import org.zalando.logbook.BodyFilters;
import org.zalando.logbook.BodyReplacers;
import org.zalando.logbook.DefaultCorrelationId;
import org.zalando.logbook.DefaultHttpLogFormatter;
import org.zalando.logbook.DefaultHttpLogWriter;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.RequestFilters;
import org.zalando.logbook.ResponseFilters;
import org.zalando.logbook.json.FastJsonHttpLogFormatter;
import org.zalando.logbook.spring.webflux.LogbookWebFilter;

import static org.zalando.logbook.json.JsonPathBodyFilters.jsonPath;

@Configuration
public class WebConfiguration implements WebFluxConfigurer
{
//    @Override
//    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer)
//    {
//        DefaultPartHttpMessageReader reader = new DefaultPartHttpMessageReader();
//        reader.setMaxParts(10);
//        reader.setEnableLoggingRequestDetails(true);
//    }

    @Bean
    public WebFilter logbookFilter(Logbook logbook)
    {
        return new LogbookWebFilter(logbook);
    }

    @Bean
    public Logbook logbook()
    {
        Logbook logbook = Logbook.builder()
                .bodyFilter(jsonPath("$.password").replace("XXX"))
                .correlationId(new DefaultCorrelationId())
                .sink(new DefaultSink(
                        new FastJsonHttpLogFormatter(),
                        new DefaultHttpLogWriter()
                ))
                .build();
        return logbook;
    }
}
