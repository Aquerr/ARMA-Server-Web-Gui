package pl.bartlomiejstepien.armaserverwebgui.application.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.auth.AswgAuthentication;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserSessionService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserDetails;

import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider
{
    private final UserSessionService userSessionService;
    private final JwtService jwtService;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        try
        {
            Jws<Claims> jws = jwtService.validateJwt(String.valueOf(authentication.getCredentials()));
            return userSessionService.getUserSession(jws.getPayload().getSubject())
                    .map(this::toAuthentication)
                    .orElse(null);
        }
        catch (Exception exception)
        {
            throw new BadCredentialsException("Bad auth token!");
        }
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return AswgAuthentication.class.isAssignableFrom(authentication);
    }

    private AswgAuthentication toAuthentication(AswgUserDetails aswgUserDetails)
    {
        return AswgAuthentication.authenticated(
                aswgUserDetails,
                prepareAuthorities(aswgUserDetails)
        );
    }

    private List<SimpleGrantedAuthority> prepareAuthorities(AswgUserDetails aswgUserDetails)
    {
        return aswgUserDetails.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getCode()))
                .toList();
    }
}
