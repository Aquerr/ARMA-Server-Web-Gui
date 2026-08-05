package pl.bartlomiejstepien.armaserverwebgui.application.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpHeaders;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;
import pl.bartlomiejstepien.armaserverwebgui.application.tracing.HttpTracingFields;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;

/**
 * Sink that produces a log and puts values in MDC so that they are available during entire thread execution.
 */
@Slf4j
public record AswgLogbookSink(Environment environment) implements Sink
{
    @Override
    public void write(Precorrelation precorrelation, HttpRequest request) throws IOException
    {
        try
        {
            AswgHttpLog aswgHttpLog = toAswgHttpLog(precorrelation.getId(), request);
            putInMdc(aswgHttpLog);
            if (environment.acceptsProfiles(Profiles.of("file-json-logs")))
            {
                log.info("Server request: {}", aswgHttpLog.requestBody());
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
                log.info("Server response: {}", aswgHttpLog.responseBody());
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
        finally
        {
            clearMdc();
        }
    }

    private static AswgHttpLog toAswgHttpLog(String correlationId,
                                             HttpRequest request) throws IOException
    {
        return AswgHttpLog.builder()
                .requestIpAddress(MDC.get(HttpTracingFields.IP_ADDRESS.getFieldName())) // Set in IpAddressMdcHttpFilter
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
                .requestDurationMilis(String.valueOf(duration.toMillis()))
                .responseContentType(response.getContentType())
                .responseBody(response.getBodyAsString())
                .responseStatus(String.format("%s %s", response.getStatus(), response.getReasonPhrase()))
                .userId(MDC.get(HttpTracingFields.USER_ID.getFieldName()))
                .build();
    }

    private void putInMdc(AswgHttpLog httpLog)
    {
        MDC.put(HttpTracingFields.CORRELATION_ID.getFieldName(), httpLog.correlationId());
        MDC.put(HttpTracingFields.URI.getFieldName(), httpLog.requestUri());
        MDC.put(HttpTracingFields.CONTENT_TYPE.getFieldName(), httpLog.requestContentType());
        MDC.put(HttpTracingFields.USER_AGENT.getFieldName(), httpLog.requestUserAgent());
        MDC.put(HttpTracingFields.IP_ADDRESS.getFieldName(), httpLog.requestIpAddress());
        MDC.put(HttpTracingFields.DURATION.getFieldName(), httpLog.requestDurationMilis());
        MDC.put(HttpTracingFields.METHOD.getFieldName(), httpLog.method());
        MDC.put(HttpTracingFields.RESPONSE_CONTENT_TYPE.getFieldName(), httpLog.responseContentType());
        MDC.put(HttpTracingFields.STATUS.getFieldName(), httpLog.responseStatus());
        MDC.put(HttpTracingFields.REQUEST_HOST.getFieldName(), httpLog.requestHost());
    }

    private void clearMdc()
    {
        Arrays.stream(HttpTracingFields.values()).forEach(field -> MDC.remove(field.getFieldName()));
    }
}