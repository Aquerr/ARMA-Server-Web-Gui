package pl.bartlomiejstepien.armaserverwebgui.application.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthenticationEntryPoint;
import pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.JwtAuthenticationConverter;
import pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.JwtAuthenticationProvider;
import pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.filter.JwtFilter;

import java.util.List;

@EnableMethodSecurity
@Configuration(proxyBeanMethods = false)
public class SecurityConfig
{
    public static final String ANT_PATTERN_ALL_API_ENDPOINTS = "/api/**";

    @EnableWebSecurity
    @ConditionalOnProperty(value = "aswg.security.enabled", havingValue = "true")
    @Configuration(proxyBeanMethods = false)
    public static class EnabledSecurityConfiguration
    {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http,
                                               AswgAuthenticationEntryPoint aswgAuthenticationEntryPoint,
                                               AuthenticationManager authenticationManager,
                                               JwtAuthenticationConverter jwtAuthenticationConverter
        ) throws Exception
        {
            http.authorizeHttpRequests(auths ->
                    {
                        auths.requestMatchers("/api/v1/auth").permitAll();
                        auths.requestMatchers("/api/v1/auth/logout").permitAll();
                        auths.requestMatchers("/api/v1/ws/**").permitAll();
                        auths.requestMatchers("/api/v1/logging/logs-sse").permitAll();
                        auths.requestMatchers("/api/v1/actuator/info").permitAll();
                        auths.requestMatchers("/api/v1/actuator/health").permitAll();
                        auths.requestMatchers(ANT_PATTERN_ALL_API_ENDPOINTS).authenticated();
                        auths.requestMatchers("/ws/**").permitAll();
                        auths.requestMatchers("/assets/**").permitAll();
                        auths.requestMatchers("/static/**").permitAll();
                        auths.requestMatchers("/public/**").permitAll();

                        auths.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                        auths.requestMatchers("/*").permitAll();
                    })
                    .anonymous(AbstractHttpConfigurer::disable)
                    .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .logout(AbstractHttpConfigurer::disable)
                    .addFilterAfter(new JwtFilter(authenticationManager, jwtAuthenticationConverter), LogoutFilter.class)
                    .formLogin((AbstractHttpConfigurer::disable))
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .exceptionHandling(exceptionHandlingSpec ->
                    {
                        exceptionHandlingSpec.accessDeniedHandler(new AccessDeniedHandlerImpl());
                        exceptionHandlingSpec.authenticationEntryPoint(aswgAuthenticationEntryPoint);
                    })
                    .cors(Customizer.withDefaults())
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(JwtAuthenticationProvider jwtAuthenticationProvider)
        {
            return new ProviderManager(jwtAuthenticationProvider);
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource()
        {
            CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
            corsConfiguration.setAllowedMethods(List.of(
                    HttpMethod.GET.name(),
                    HttpMethod.HEAD.name(),
                    HttpMethod.POST.name(),
                    HttpMethod.DELETE.name(),
                    HttpMethod.PUT.name())
            );
            corsConfiguration.setAllowedOrigins(List.of("*"));

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", corsConfiguration);
            return source;
        }

        @Bean
        public PasswordEncoder passwordEncoder()
        {
            return new BCryptPasswordEncoder();
        }
    }

    @EnableWebSecurity
    @ConditionalOnProperty(value = "aswg.security.enabled", matchIfMissing = true, havingValue = "false")
    @Configuration(proxyBeanMethods = false)
    public static class DisabledSecurityConfiguration
    {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
        {
            return http.cors(Customizer.withDefaults())
                    .authorizeHttpRequests(authorizeRequests ->
                    {
                        authorizeRequests.requestMatchers("/**").permitAll();
                    })
                    .csrf(AbstractHttpConfigurer::disable)
                    .build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource()
        {
            CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
            corsConfiguration.setAllowedMethods(List.of(
                    HttpMethod.GET.name(),
                    HttpMethod.HEAD.name(),
                    HttpMethod.POST.name(),
                    HttpMethod.DELETE.name(),
                    HttpMethod.PUT.name())
            );
            corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", corsConfiguration);
            return source;
        }

        @Bean
        public PasswordEncoder passwordEncoder()
        {
            return NoOpPasswordEncoder.getInstance();
        }
    }
}
