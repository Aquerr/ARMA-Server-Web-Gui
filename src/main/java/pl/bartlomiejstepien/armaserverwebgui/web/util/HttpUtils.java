package pl.bartlomiejstepien.armaserverwebgui.web.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HttpUtils
{
    public static String retrieveIpAddress(final HttpServletRequest request)
    {
        if (request == null)
            return null;

        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null)
            return xForwardedForHeader;

        return request.getRemoteAddr();
    }

    private HttpUtils()
    {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
