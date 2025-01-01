package pl.bartlomiejstepien.armaserverwebgui.application.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.config.security.JwtService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService
{
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;

    public Mono<JwtToken> authenticate(String username, String password, String ipAddress)
    {
        log.info("Login attempt for {} from {}", username, ipAddress);
        return userService.getUserWithPassword(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid username or password")))
                .flatMap(user -> this.userService.getUser(username))
                .map(user -> new JwtToken(this.jwtService.createJwt(user), user.getAuthorities()));
    }

    public Mono<Void> logout(String jwt)
    {
        return this.jwtService.invalidate(jwt);
    }
}
