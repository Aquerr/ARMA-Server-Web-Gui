package pl.bartlomiejstepien.armaserverwebgui.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.WebFilter;
import org.zalando.logbook.DefaultCorrelationId;
import org.zalando.logbook.DefaultHttpLogWriter;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.json.FastJsonHttpLogFormatter;
import org.zalando.logbook.spring.webflux.LogbookWebFilter;

import static org.zalando.logbook.json.JsonPathBodyFilters.jsonPath;

@Configuration
public class WebConfiguration implements WebFluxConfigurer
{
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
                .bodyFilter(jsonPath("$.publishedFileDetails").delete())
                .correlationId(new DefaultCorrelationId())
                .sink(new DefaultSink(
                        new FastJsonHttpLogFormatter(),
                        new DefaultHttpLogWriter()
                ))
                .build();
        return logbook;
    }
}
