package pl.bartlomiejstepien.armaserverwebgui.application;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.bartlomiejstepien.armaserverwebgui.application.tracing.HttpTracingFields;
import pl.bartlomiejstepien.armaserverwebgui.web.util.HttpUtils;

import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@Component
public class IpAddressMdcHttpFilter extends OncePerRequestFilter
{
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        try
        {
            registerIpAddressInMdc(request);
            filterChain.doFilter(request, response);
        }
        catch (Exception exception)
        {
            throw exception;
        }
        finally
        {
            clearIpAddressInMdc();
        }
    }

    private void registerIpAddressInMdc(HttpServletRequest request)
    {
        String ipAddress = HttpUtils.retrieveIpAddress(request);
        MDC.put(HttpTracingFields.IP_ADDRESS.getFieldName(), ipAddress);
    }

    private void clearIpAddressInMdc()
    {
        MDC.remove(HttpTracingFields.IP_ADDRESS.getFieldName());
    }
}
