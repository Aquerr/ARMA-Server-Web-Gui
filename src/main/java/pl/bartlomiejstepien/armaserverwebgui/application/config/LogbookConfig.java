package pl.bartlomiejstepien.armaserverwebgui.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.WebFilter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.BodyFilters;
import org.zalando.logbook.core.DefaultCorrelationId;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.core.ResponseFilters;
import org.zalando.logbook.json.FastJsonHttpLogFormatter;
import org.zalando.logbook.spring.webflux.LogbookWebFilter;

import static org.zalando.logbook.core.Conditions.contentType;
import static org.zalando.logbook.json.JsonPathBodyFilters.jsonPath;

@Configuration
public class LogbookConfig implements WebFluxConfigurer
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
                .responseFilter(ResponseFilters.replaceBody(response -> contentType("text/html", "text/javascript", "application/javascript").test(response) ? "<skipped>" : null))
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
