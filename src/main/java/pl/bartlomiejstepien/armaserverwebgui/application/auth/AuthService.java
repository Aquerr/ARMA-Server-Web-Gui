package pl.bartlomiejstepien.armaserverwebgui.application.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.security.exception.BadCredentialsException;
import pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.JwtService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService
{
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;

    public JwtToken authenticate(String username, String password, String ipAddress)
    {
        log.info("Login attempt for {} from {}", username, ipAddress);
        AswgUserWithPassword aswgUserWithPassword = userService.getUserWithPassword(username);

        if (aswgUserWithPassword == null)
            throw new BadCredentialsException();
        if (!passwordEncoder.matches(password, aswgUserWithPassword.getPassword()))
            throw new BadCredentialsException();

        AswgUser user = this.userService.getUser(username);
        return new JwtToken(this.jwtService.createJwt(user), user.getAuthorities());
    }

    public void logout(String jwt)
    {
        this.jwtService.invalidate(jwt);
    }
}
