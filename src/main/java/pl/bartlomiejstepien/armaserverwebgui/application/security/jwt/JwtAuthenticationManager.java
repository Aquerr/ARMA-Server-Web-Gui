package pl.bartlomiejstepien.armaserverwebgui.application.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationManager implements AuthenticationManager
{
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public Authentication authenticate(Authentication authentication)
    {
        try
        {
            Jws<Claims> jws = jwtService.validateJwt(String.valueOf(authentication.getCredentials()));
            return Optional.ofNullable(userService.getUser(jws.getPayload().getSubject()))
                    .map(this::toAuthentication)
                    .orElse(null);
        }
        catch (Exception exception)
        {
            throw new BadCredentialsException("Bad auth token!");
        }
    }

    //TODO: Create custom Authentication implementation or store AswgUser in UsernamePasswordAuthenticationToken
    private Authentication toAuthentication(AswgUser aswgUser)
    {
        return UsernamePasswordAuthenticationToken.authenticated(
                aswgUser,
                aswgUser.getUsername(),
                prepareAuthorities(aswgUser)
        );
    }

    private List<SimpleGrantedAuthority> prepareAuthorities(AswgUser aswgUser)
    {
        return aswgUser.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getCode()))
                .toList();
    }
}
