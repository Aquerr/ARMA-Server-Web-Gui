package pl.bartlomiejstepien.armaserverwebgui.controller;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public Mono<ResponseEntity<Void>> authenticate(@RequestBody UserCredentials userCredentials)
    {
        return Mono.justOrEmpty(userService.authenticate(userCredentials.getUsername(), userCredentials.getPassword()))
                .map(jwt -> {
                    ResponseCookie authCookie = ResponseCookie.fromClientResponse("X-Auth", jwt)
                            .maxAge(3600)
                            .httpOnly(true)
                            .path("/")
                            .secure(false)
                            .build();

                    return ResponseEntity.noContent()
                            .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                            .<Void>build();
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
}
