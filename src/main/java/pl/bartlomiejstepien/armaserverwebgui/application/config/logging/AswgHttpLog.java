package pl.bartlomiejstepien.armaserverwebgui.application.config.logging;

import lombok.Builder;

@Builder(toBuilder = true)
public record AswgHttpLog(String correlationId, String requestHost, String requestUri, String requestContentType, String requestUserAgent,
                          String requestIpAddress, String requestDurationMilis, String requestBody, String method, String userId, String responseBody,
                          String responseContentType, String responseStatus)
{
}