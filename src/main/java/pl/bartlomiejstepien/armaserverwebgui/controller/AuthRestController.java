package pl.bartlomiejstepien.armaserverwebgui.controller;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.armaserverwebgui.model.UserProfile;
import pl.bartlomiejstepien.armaserverwebgui.service.UserService;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthRestController
{
    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<JwtTokenResponse>> authenticate(@RequestBody UserCredentials userCredentials)
    {
        return Mono.justOrEmpty(userService.authenticate(userCredentials.getUsername(), userCredentials.getPassword()))
                .map(jwt -> {
                    return ResponseEntity.ok()
                            .body(JwtTokenResponse.of(jwt));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @GetMapping("/myself")
    public Mono<ResponseEntity<UserProfile>> getMyself(Principal principal)
    {
        return Mono.justOrEmpty(this.userService.getUserProfile(principal.getName()))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @Value
    static class UserCredentials
    {
        String username;
        String password;
    }

    @Value(staticConstructor = "of")
    private static class JwtTokenResponse
    {
        String value;
    }
}
