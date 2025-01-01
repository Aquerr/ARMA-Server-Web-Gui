package pl.bartlomiejstepien.armaserverwebgui.application.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager
{
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication)
    {
        return Mono.just(authentication)
                .map(authentication1 -> jwtService.validateJwt(String.valueOf(authentication1.getCredentials())))
                .onErrorResume(Exception.class, err -> Mono.error(new BadCredentialsException("Bad auth token!")))
                .flatMap(jws -> userService.getUser(jws.getPayload().getSubject()))
                .mapNotNull(this::toAuthentication);
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
