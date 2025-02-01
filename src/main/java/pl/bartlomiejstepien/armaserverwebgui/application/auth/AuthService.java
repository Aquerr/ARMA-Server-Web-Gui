package pl.bartlomiejstepien.armaserverwebgui.application.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.config.security.JwtService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService
{
//    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;

    public JwtToken authenticate(String username, String password, String ipAddress)
    {
        log.info("Login attempt for {} from {}", username, ipAddress);
        AswgUserWithPassword aswgUserWithPassword = userService.getUserWithPassword(username);
//        if (!passwordEncoder.matches(password, aswgUserWithPassword.getPassword()))
//            throw new BadCredentialsException("Invalid username or password");

        AswgUser user = this.userService.getUser(username);
        return new JwtToken(this.jwtService.createJwt(user), user.getAuthorities());
    }

    public void logout(String jwt)
    {
        this.jwtService.invalidate(jwt);
    }
}
