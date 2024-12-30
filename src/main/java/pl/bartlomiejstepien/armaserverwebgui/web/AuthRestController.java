package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.model.UserProfile;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import pl.bartlomiejstepien.armaserverwebgui.web.util.HttpUtils;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthRestController
{
    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<JwtTokenResponse>> authenticate(
            @RequestBody UserCredentials userCredentials,
            ServerHttpRequest request)
    {
        return Mono.fromCallable(() -> userService.authenticate(
                        userCredentials.username(),
                        userCredentials.password(),
                        HttpUtils.retriveIpAddress(request)))
                .onErrorResume(throwable -> Mono.empty())
                .map(jwt -> ResponseEntity.ok().body(new JwtTokenResponse(jwt)))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @GetMapping("/myself")
    public Mono<ResponseEntity<UserProfile>> getMyself(Principal principal)
    {
        return Mono.justOrEmpty(this.userService.getUserProfile(principal.getName()))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    public record UserCredentials(String username, String password) { }

    public record JwtTokenResponse(String value) {}
}
