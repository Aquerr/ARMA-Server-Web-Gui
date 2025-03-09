package pl.bartlomiejstepien.armaserverwebgui.application.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.zalando.logbook.servlet.SecureLogbookFilter;
import pl.bartlomiejstepien.armaserverwebgui.application.tracing.HttpTracingFields;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import static org.zalando.logbook.core.Conditions.contentType;
import static org.zalando.logbook.core.Conditions.exclude;
import static org.zalando.logbook.core.Conditions.requestTo;

@Configuration(proxyBeanMethods = false)
public class LogbookConfig
{
    private static final String[] IGNORED_FILE_CONTENT = new String[]{"text/html", "text/css", "text/javascript", "application/javascript", "image/*"};

    @Bean
    public LogbookFilter logbookFilter(Logbook logbook)
    {
        return new LogbookFilter(logbook);
    }

    @Bean
    public SecureLogbookFilter secureLogbookFilter(Logbook logbook)
    {
        return new SecureLogbookFilter(logbook);
    }

    @Bean
    public Logbook logbook(ObjectMapper objectMapper)
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
                .sink(new AswgLogbookSink())
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

    /**
     * Sink that produces a log and puts values in MDC so that they are available during entire thread execution.
     */
    @Slf4j
    private static class AswgLogbookSink implements Sink
    {
        @Override
        public void write(Precorrelation precorrelation, HttpRequest request) throws IOException
        {
            AswgHttpLog aswgHttpLog = toAswgHttpLog(precorrelation.getId(), request);
            putInMdc(aswgHttpLog);
            log.info("Server request: {}", aswgHttpLog.getRequestBody());
        }

        @Override
        public void write(Correlation correlation, HttpRequest request, HttpResponse response) throws IOException
        {
            AswgHttpLog aswgHttpLog = toAswgHttpLog(correlation.getId(), correlation.getDuration(), request, response);
            putInMdc(aswgHttpLog);
            log.info("Server response: {}", aswgHttpLog.getResponseBody());
        }

        private static AswgHttpLog toAswgHttpLog(String correlationId,
                                                 HttpRequest request) throws IOException
        {
            return AswgHttpLog.builder()
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
            MDC.put(HttpTracingFields.USER_AGENT.getFieldName(), httpLog.getRequestContentType());
            MDC.put(HttpTracingFields.DURATION.getFieldName(), Optional.ofNullable(httpLog.getRequestDuration())
                    .map(Duration::toMillis)
                    .map(String::valueOf)
                    .orElse(null));
            MDC.put(HttpTracingFields.BODY.getFieldName(), httpLog.getRequestBody());
            MDC.put(HttpTracingFields.METHOD.getFieldName(), httpLog.getMethod());
            MDC.put(HttpTracingFields.RESPONSE_BODY.getFieldName(), httpLog.getResponseBody());
            MDC.put(HttpTracingFields.RESPONSE_CONTENT_TYPE.getFieldName(), httpLog.getResponseContentType());
            MDC.put(HttpTracingFields.STATUS.getFieldName(), httpLog.getResponseStatus());
        }
    }

    @Builder(toBuilder = true)
    @Value
    private static class AswgHttpLog
    {
        String correlationId;
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
