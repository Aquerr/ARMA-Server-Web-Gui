package pl.bartlomiejstepien.armaserverwebgui.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig
{
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http,
                                              ReactiveAuthenticationManager jwtAuthenticationManager,
                                              JwtServerAuthenticationConverter jwtServerAuthenticationConverter)
    {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter);


        http.authorizeExchange(auths -> {
            auths.pathMatchers("/api/v1/auth").permitAll();
            auths.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll();
            auths.anyExchange().authenticated();
        })
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .formLogin().disable()
            .httpBasic().disable()
        .cors().and()
        .csrf().disable();
        return http.build();
    }

    @Bean
    public ReactiveAuthenticationManager jwtAuthenticationManager(JwtService jwtService)
    {
        return authentication -> {
            return Mono.just(authentication)
                    .map(authentication1 -> jwtService.validateJwt(String.valueOf(authentication1.getCredentials())))
                    .onErrorResume(Exception.class, err -> Mono.error(new BadCredentialsException("Bad auth token!")))
                    .mapNotNull(jws -> {
                        return new UsernamePasswordAuthenticationToken(
                                jws.getBody().getSubject(),
                                String.valueOf(authentication.getCredentials()),
                                List.of(new SimpleGrantedAuthority("USER"))); // Needed to make user authenticated!
                    });
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.setAllowedMethods(List.of(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(), HttpMethod.DELETE.name()));
//        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
        corsConfiguration.setAllowedOrigins(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
