package pl.bartlomiejstepien.armaserverwebgui.application.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class SecurityConfig
{
    @EnableWebFluxSecurity
    @ConditionalOnProperty(value = "aswg.security.enabled", havingValue = "true")
    public static class EnabledSecurityConfiguration
    {
        @Bean
        public SecurityWebFilterChain filterChain(ServerHttpSecurity http,
                                                  ReactiveAuthenticationManager jwtAuthenticationManager,
                                                  JwtServerAuthenticationConverter jwtServerAuthenticationConverter)
        {
            AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
            authenticationWebFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter);
            authenticationWebFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)));

            http.authorizeExchange(auths -> {
                        auths.pathMatchers("/api/v1/auth").permitAll();
                        auths.pathMatchers("/api/v1/ws/**").permitAll();
                        auths.pathMatchers("/api/**").authenticated();
                        auths.pathMatchers("/ws/**").permitAll();
                        auths.pathMatchers("/static/**").permitAll();
                        auths.pathMatchers("/public/**").permitAll();
                        auths.pathMatchers("/api/v1/actuator/metrics").authenticated();
                        auths.pathMatchers("/api/v1/actuator/**").permitAll();
                        auths.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                        auths.pathMatchers("/*").permitAll();
                    })
                    .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                    .formLogin((ServerHttpSecurity.FormLoginSpec::disable))
                    .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                    .exceptionHandling(exceptionHandlingSpec -> {
                        exceptionHandlingSpec.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED));
                    })
                    .cors(Customizer.withDefaults())
                    .csrf(ServerHttpSecurity.CsrfSpec::disable);
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
            corsConfiguration.setAllowedOrigins(List.of("*"));

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", corsConfiguration);
            return source;
        }
    }

    @EnableWebFluxSecurity
    @ConditionalOnProperty(value = "aswg.security.enabled", matchIfMissing = true, havingValue = "false")
    public static class DisabledSecurityConfiguration
    {
        @Bean
        public SecurityWebFilterChain filterChain(ServerHttpSecurity http)
        {
            http.cors(Customizer.withDefaults())
                .headers(headerSpec -> {
                    headerSpec.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable);
                })
                .authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec.pathMatchers("/**").permitAll();
                })
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
            return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource()
        {
            CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
            corsConfiguration.setAllowedMethods(List.of(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(), HttpMethod.DELETE.name()));
            corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", corsConfiguration);
            return source;
        }
    }
}
