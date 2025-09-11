package pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.bartlomiejstepien.armaserverwebgui.application.security.exception.BadAuthTokenException;
import pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.JwtService;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String jwt = jwtService.extractJwt(request);
        if (jwt == null)
        {
            filterChain.doFilter(request, response);
            return;
        }

        try
        {
            Authentication authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(jwt, jwt));
            authenticate(authentication);
            filterChain.doFilter(request, response);
        }
        catch (Exception exception)
        {
            if (exception instanceof BadCredentialsException)
            {
                throw new BadAuthTokenException(exception);
            }
            else throw exception;
        }
    }

    private void authenticate(Authentication authentication)
    {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
