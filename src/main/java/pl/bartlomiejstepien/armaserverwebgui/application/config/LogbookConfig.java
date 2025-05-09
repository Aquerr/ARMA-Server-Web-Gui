package pl.bartlomiejstepien.armaserverwebgui.application.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpHeaders;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.ContentType;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;
import org.zalando.logbook.core.DefaultCorrelationId;
import org.zalando.logbook.core.ResponseFilters;
import org.zalando.logbook.json.JsonBodyFilters;
import org.zalando.logbook.servlet.LogbookFilter;
import pl.bartlomiejstepien.armaserverwebgui.application.tracing.HttpTracingFields;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

import static org.zalando.logbook.core.Conditions.contentType;
import static org.zalando.logbook.core.Conditions.exclude;
import static org.zalando.logbook.core.Conditions.requestTo;

@Configuration(proxyBeanMethods = false)
public class LogbookConfig
{
    private static final String[] IGNORED_FILE_CONTENT = new String[] {"text/html", "text/css", "text/javascript", "application/javascript", "image/*"};

    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public FilterRegistrationBean<LogbookFilter> logbookFilterFilterRegistrationBean(Logbook logbook)
    {
        FilterRegistrationBean<LogbookFilter> registrationBean = new FilterRegistrationBean<>(new LogbookFilter(logbook));
        registrationBean.setName("logbookFilter");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    @Bean
    public Logbook logbook(ObjectMapper objectMapper, Environment environment)
    {
        return Logbook.builder()
                .responseFilter(ResponseFilters.replaceBody(response -> contentType("text/html", IGNORED_FILE_CONTENT).test(response) ? "<skipped>" : null))
                .bodyFilter(JsonBodyFilters.replaceJsonStringProperty(Set.of("password"), "XXX"))
                .condition(exclude(
                        requestTo("/api/v1/logging/latest-logs"),
                        requestTo("/api/v1/logging/logs-sse"))
                )
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

                if (foundJsonNode.isContainerNode())
                {
                    ((ContainerNode<?>) foundJsonNode).removeAll();
                }

                return objectMapper.writeValueAsString(objectNode);
            }
            catch (JsonProcessingException e)
            {
                return body;
            }
        }
    }

    /**
     * Sink that produces a log and puts values in MDC so that they are available during entire thread execution.
     */
    @Slf4j
    @RequiredArgsConstructor
    private static class AswgLogbookSink implements Sink
    {
        private final Environment environment;

        @Override
        public void write(Precorrelation precorrelation, HttpRequest request) throws IOException
        {
            try
            {
                AswgHttpLog aswgHttpLog = toAswgHttpLog(precorrelation.getId(), request);
                putInMdc(aswgHttpLog);
                if (environment.acceptsProfiles(Profiles.of("file-json-logs")))
                {
                    log.info("Server request {}", aswgHttpLog.getRequestBody());
                }
                else
                {
                    log.info("Server request: {}", aswgHttpLog);
                }
            }
            catch (Exception e)
            {
                throw new IOException(e);
            }
        }

        @Override
        public void write(Correlation correlation, HttpRequest request, HttpResponse response) throws IOException
        {
            try
            {
                AswgHttpLog aswgHttpLog = toAswgHttpLog(correlation.getId(), correlation.getDuration(), request, response);
                putInMdc(aswgHttpLog);
                if (environment.acceptsProfiles(Profiles.of("file-json-logs")))
                {
                    log.info("Server response {}", aswgHttpLog.getResponseBody());
                }
                else
                {
                    log.info("Server response: {}", aswgHttpLog);
                }
            }
            catch (Exception e)
            {
                throw new IOException(e);
            }
        }

        private static AswgHttpLog toAswgHttpLog(String correlationId,
                                                 HttpRequest request) throws IOException
        {
            return AswgHttpLog.builder()
                    .requestHost(request.getHost())
                    .requestUri(request.getRequestUri())
                    .method(request.getMethod())
                    .requestBody(request.getBodyAsString())
                    .requestContentType(request.getContentType())
                    .correlationId(correlationId)
                    .requestUserAgent(request.getHeaders().getFirst(HttpHeaders.USER_AGENT))
                    .build();
        }

        private static AswgHttpLog toAswgHttpLog(String correlationId,
                                                 Duration duration,
                                                 HttpRequest request,
                                                 HttpResponse response) throws IOException
        {
            return toAswgHttpLog(correlationId, request)
                    .toBuilder()
                    .requestDuration(duration)
                    .responseContentType(response.getContentType())
                    .responseBody(response.getBodyAsString())
                    .responseStatus(String.format("%s %s", response.getStatus(), response.getReasonPhrase()))
                    .build();
        }

        private void putInMdc(AswgHttpLog httpLog)
        {
            MDC.put(HttpTracingFields.CORRELATION_ID.getFieldName(), httpLog.getCorrelationId());
            MDC.put(HttpTracingFields.URI.getFieldName(), httpLog.getRequestUri());
            MDC.put(HttpTracingFields.CONTENT_TYPE.getFieldName(), httpLog.getRequestContentType());
            MDC.put(HttpTracingFields.USER_AGENT.getFieldName(), httpLog.getRequestUserAgent());
            MDC.put(HttpTracingFields.DURATION.getFieldName(), Optional.ofNullable(httpLog.getRequestDuration())
                    .map(Duration::toMillis)
                    .map(String::valueOf)
                    .orElse(null));
            MDC.put(HttpTracingFields.METHOD.getFieldName(), httpLog.getMethod());
            MDC.put(HttpTracingFields.RESPONSE_CONTENT_TYPE.getFieldName(), httpLog.getResponseContentType());
            MDC.put(HttpTracingFields.STATUS.getFieldName(), httpLog.getResponseStatus());
            MDC.put(HttpTracingFields.REUQEST_HOST.getFieldName(), httpLog.getRequestHost());
        }
    }

    @Builder(toBuilder = true)
    @Value
    private static class AswgHttpLog
    {
        String correlationId;
        String requestHost;
        String requestUri;
        String requestContentType;
        String requestUserAgent;
        Duration requestDuration;
        String requestBody;
        String method;
        String responseBody;
        String responseContentType;
        String responseStatus;
    }
}
