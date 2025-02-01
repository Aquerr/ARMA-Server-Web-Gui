package pl.bartlomiejstepien.armaserverwebgui.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.auth.AuthService;
import pl.bartlomiejstepien.armaserverwebgui.application.auth.JwtToken;
import pl.bartlomiejstepien.armaserverwebgui.application.config.security.JwtService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.web.response.UserProfileResponse;
import pl.bartlomiejstepien.armaserverwebgui.web.util.HttpUtils;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthRestController
{
    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<JwtTokenResponse> authenticate(
            @RequestBody UserCredentials userCredentials,
            HttpServletRequest request)
    {
        JwtToken jwtToken = authService.authenticate(
                userCredentials.username(),
                userCredentials.password(),
                HttpUtils.retrieveIpAddress(request)
        );

        return ResponseEntity.ok(new JwtTokenResponse(jwtToken.jwt(), jwtToken.authorities()));
    }

    @GetMapping("/myself")
    public ResponseEntity<UserProfileResponse> getMyself(Principal principal)
    {
        return Optional.ofNullable(this.userService.getUser(principal.getName()))
                .map(this::toUserProfileResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> invalidateToken(HttpServletRequest request)
    {
        Optional.ofNullable(jwtService.extractJwt(request))
                .ifPresent(this.authService::logout);

        return ResponseEntity.ok().build();
    }

    private UserProfileResponse toUserProfileResponse(AswgUser aswgUser)
    {
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setUsername(aswgUser.getUsername());
        userProfileResponse.setCreateDate(aswgUser.getCreatedDate());
        userProfileResponse.setAuthorities(aswgUser.getAuthorities().stream()
                .map(AswgAuthority::getCode)
                .collect(Collectors.toSet()));
        return userProfileResponse;
    }

    public record UserCredentials(String username, String password) { }

    public record JwtTokenResponse(String jwt, Set<AswgAuthority> authorities) {}
}
