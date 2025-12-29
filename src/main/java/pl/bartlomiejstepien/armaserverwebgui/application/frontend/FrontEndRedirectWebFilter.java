package pl.bartlomiejstepien.armaserverwebgui.application.frontend;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 2)
public class FrontEndRedirectWebFilter extends OncePerRequestFilter
{
    private boolean isNotApiRequest(String uri)
    {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uri).build();
        String path = uriComponents.getPath();
        if (path == null)
            return false;
        return !path.startsWith("/api") && path.matches("[^\\\\.]*");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String path = request.getRequestURI();
        if (isNotApiRequest(path))
        {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
