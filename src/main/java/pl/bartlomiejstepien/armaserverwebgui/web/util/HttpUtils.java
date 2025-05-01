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

        String xforwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xforwardedForHeader != null)
            return xforwardedForHeader;

        return request.getRemoteAddr();
    }

    private HttpUtils()
    {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
