package pl.bartlomiejstepien.armaserverwebgui.web.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

@Slf4j
public final class HttpUtils
{
    public static String retrieveIpAddress(final ServerHttpRequest request)
    {
        if (request == null)
            return null;

        String xForwardedForHeader = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedForHeader != null)
            return xForwardedForHeader;

        return Optional.ofNullable(request.getRemoteAddress()).map(InetSocketAddress::getAddress)
                .map(InetAddress::getHostAddress)
                .orElse(null);
    }

    private HttpUtils()
    {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
