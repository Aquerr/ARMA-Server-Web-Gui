package pl.bartlomiejstepien.armaserverwebgui.interfaces.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import pl.bartlomiejstepien.armaserverwebgui.application.auth.AuthService;
import pl.bartlomiejstepien.armaserverwebgui.application.config.security.JwtService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.web.response.UserProfileResponse;
import pl.bartlomiejstepien.armaserverwebgui.web.util.HttpUtils;
import reactor.core.publisher.Mono;

import java.security.Principal;
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
    public Mono<ResponseEntity<JwtTokenResponse>> authenticate(
            @RequestBody UserCredentials userCredentials,
            ServerHttpRequest request)
    {
        return authService.authenticate(
                        userCredentials.username(),
                        userCredentials.password(),
                        HttpUtils.retriveIpAddress(request))
                .map(jwt -> ResponseEntity.ok().body(new JwtTokenResponse(jwt.jwt(), jwt.authorities())))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @GetMapping("/myself")
    public Mono<ResponseEntity<UserProfileResponse>> getMyself(Principal principal)
    {
        return this.userService.getUser(principal.getName())
                .map(this::toUserProfileResponse)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @PostMapping("/logout")
    public Mono<Void> invalidateToken(ServerWebExchange request)
    {
        return Mono.just(request)
                .map(jwtService::extractJwt)
                .flatMap(this.authService::logout);
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
