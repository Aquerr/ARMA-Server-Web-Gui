package pl.bartlomiejstepien.armaserverwebgui.application.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.WebFilter;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.ContentType;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.DefaultCorrelationId;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.core.ResponseFilters;
import org.zalando.logbook.json.FastJsonHttpLogFormatter;
import org.zalando.logbook.json.JsonBodyFilters;
import org.zalando.logbook.spring.webflux.LogbookWebFilter;

import javax.annotation.Nullable;
import java.util.Set;

import static org.zalando.logbook.core.Conditions.contentType;
import static org.zalando.logbook.core.Conditions.exclude;
import static org.zalando.logbook.core.Conditions.requestTo;

@Configuration
public class LogbookConfig implements WebFluxConfigurer
{
    private static final String[] IGNORED_FILE_CONTENT = new String[]{"text/html", "text/css", "text/javascript", "application/javascript", "image/*"};

    @Bean
    public WebFilter logbookFilter(Logbook logbook)
    {
        return new LogbookWebFilter(logbook);
    }

    @Bean
    public Logbook logbook(ObjectMapper objectMapper)
    {
        Logbook logbook = Logbook.builder()
                .responseFilter(ResponseFilters.replaceBody(response -> contentType("text/html", IGNORED_FILE_CONTENT).test(response) ? "<skipped>" : null))
                .bodyFilter(JsonBodyFilters.replaceJsonStringProperty(Set.of("password"), "XXX"))
                .condition(exclude(
                        requestTo("/api/v1/logging/latest-logs"),
                        requestTo("/api/v1/logging/logs-sse"))
                )
                .bodyFilter(new FilterJsonAttribute(objectMapper, "publishedFileDetails"))
                .bodyFilter(new FilterJsonAttribute(objectMapper, "content"))
                .correlationId(new DefaultCorrelationId())
                .sink(new DefaultSink(
                        new FastJsonHttpLogFormatter(),
                        new DefaultHttpLogWriter()
                ))
                .build();
        return logbook;
    }

    @AllArgsConstructor
    private static class FilterJsonAttribute implements BodyFilter
    {
        private final ObjectMapper objectMapper;
        private final String fieldName;

        @Override
        public String filter(@Nullable String contentType, String body)
        {
            if (!ContentType.isJsonMediaType(contentType))
            {
                return body;
            }

            try
            {
                ObjectNode objectNode = objectMapper.readValue(body, ObjectNode.class);

                JsonNode foundJsonNode = objectNode.findValue(fieldName);
                if (foundJsonNode == null || foundJsonNode.isEmpty())
                    return body;

                if (foundJsonNode.isContainerNode()) {
                    ((ContainerNode<?>)foundJsonNode).removeAll();
                }

                return objectMapper.writeValueAsString(objectNode);
            }
            catch (JsonProcessingException e)
            {
                return body;
            }
        }
    }
}
