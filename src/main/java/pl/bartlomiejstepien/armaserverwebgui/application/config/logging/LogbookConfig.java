package pl.bartlomiejstepien.armaserverwebgui.application.config.logging;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.ContentType;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.RequestFilter;
import org.zalando.logbook.ResponseFilter;
import org.zalando.logbook.core.DefaultCorrelationId;
import org.zalando.logbook.core.RequestFilters;
import org.zalando.logbook.core.ResponseFilters;
import org.zalando.logbook.json.JsonBodyFilters;
import org.zalando.logbook.servlet.LogbookFilter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ContainerNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.Set;
import javax.annotation.Nullable;

import static org.zalando.logbook.core.Conditions.contentType;

@Configuration(proxyBeanMethods = false)
public class LogbookConfig
{
    public static final String[] CONTENT_TYPES_WITH_SKIPPED_RESPONSE_BODY = new String[] {
            "text/html",
            "text/css",
            "text/javascript",
            "application/javascript",
            "image/*",
            "application/octet-stream"
    };

    @Bean
    public FilterRegistrationBean<LogbookFilter> logbookFilterFilterRegistrationBean(Logbook logbook)
    {
        FilterRegistrationBean<LogbookFilter> registrationBean = new FilterRegistrationBean<>(new LogbookFilter(logbook));
        registrationBean.setName("logbookFilter");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registrationBean;
    }

    @Bean
    public Logbook logbook(ObjectMapper objectMapper, Environment environment)
    {
        return Logbook.builder()
                .requestFilter(RequestFilter.merge(RequestFilters.defaultValue(),
                        RequestFilters.replaceBody(
                                response -> contentType("text/html", CONTENT_TYPES_WITH_SKIPPED_RESPONSE_BODY).test(response) ? "<skipped>" : null)))
                .responseFilter(ResponseFilter.merge(ResponseFilters.defaultValue(),
                        ResponseFilters.replaceBody(
                                response -> contentType("text/html", CONTENT_TYPES_WITH_SKIPPED_RESPONSE_BODY).test(response) ? "<skipped>" : null)))
                .bodyFilter(JsonBodyFilters.replaceJsonStringProperty(Set.of("password"), "XXX"))
                .strategy(new AswgLogBookStrategy())
                .bodyFilter(new FilterJsonAttribute(objectMapper, "publishedFileDetails"))
                .bodyFilter(new FilterJsonAttribute(objectMapper, "content"))
                .correlationId(new DefaultCorrelationId())
                .sink(new AswgLogbookSink(environment))
                .build();
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

                if (foundJsonNode.isContainer())
                {
                    ((ContainerNode<?>) foundJsonNode).removeAll();
                }

                return objectMapper.writeValueAsString(objectNode);
            }
            catch (JacksonException e)
            {
                return body;
            }
        }
    }
}
