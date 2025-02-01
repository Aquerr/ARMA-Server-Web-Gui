package pl.bartlomiejstepien.armaserverwebgui.application.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zalando.logbook.servlet.LogbookFilter;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;

import java.io.IOException;
import java.util.List;

@EnableMethodSecurity
@Configuration(proxyBeanMethods = false)
public class SecurityConfig
{
    @EnableWebSecurity
    @ConditionalOnProperty(value = "aswg.security.enabled", havingValue = "true")
    @Configuration(proxyBeanMethods = false)
    public static class EnabledSecurityConfiguration
    {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http,
                                               AuthenticationManager jwtAuthenticationManager,
                                               JwtAuthenticationConverter jwtAuthenticationConverter,
                                               LogbookFilter logbookFilter,
                                               JwtFilter jwtFilter,
                                               FrontEndRedirectWebFilter frontEndRedirectWebFilter
        ) throws Exception
        {
//            AuthenticationFilter authenticationWebFilter = new AuthenticationFilter(jwtAuthenticationManager, jwtAuthenticationConverter);
//            authenticationWebFilter.setRequestMatcher(new AntPathRequestMatcher("/api/**"));
//            authenticationWebFilter.setFailureHandler(new AuthenticationEntryPointFailureHandler(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
//            authenticationWebFilter.setSuccessHandler((request, response, authentication) -> {});

            http.authorizeHttpRequests(auths -> {
                        auths.requestMatchers("/api/v1/auth").permitAll();
                        auths.requestMatchers("/api/v1/auth/logout").permitAll();
                        auths.requestMatchers("/api/v1/ws/**").permitAll();
                        auths.requestMatchers("/api/v1/actuator/info").permitAll();
                        auths.requestMatchers("/api/v1/actuator/health").permitAll();
                        auths.requestMatchers("/api/v1/actuator/**").authenticated();
                        auths.requestMatchers("/api/**").authenticated();
                        auths.requestMatchers("/ws/**").permitAll();
                        auths.requestMatchers("/static/**").permitAll();
                        auths.requestMatchers("/public/**").permitAll();

                        auths.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                        auths.requestMatchers("/*").permitAll();
                    })
                    .anonymous(AbstractHttpConfigurer::disable)
                    .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .logout(AbstractHttpConfigurer::disable)
                    .addFilterAfter(jwtFilter, LogoutFilter.class)
                    .addFilterBefore(logbookFilter, JwtFilter.class)
                    .addFilterAfter(frontEndRedirectWebFilter, AnonymousAuthenticationFilter.class)
                    .formLogin((AbstractHttpConfigurer::disable))
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .authenticationManager(jwtAuthenticationManager)
                    .exceptionHandling(exceptionHandlingSpec -> {
                        exceptionHandlingSpec.accessDeniedHandler(new AccessDeniedHandlerImpl());
                        exceptionHandlingSpec.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                    })
                    .cors(Customizer.withDefaults())
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }

        @Bean
        public JwtAuthenticationManager jwtAuthenticationManager(UserService userService,
                                                              JwtService jwtService)
        {
            return new JwtAuthenticationManager(userService, jwtService);
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource()
        {
            CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
            corsConfiguration.setAllowedMethods(List.of(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(), HttpMethod.DELETE.name(), HttpMethod.PUT.name()));
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
                            .authorizeHttpRequests(authorizeRequests -> {
                               authorizeRequests.requestMatchers("/**").permitAll();
                            })
                    .csrf(AbstractHttpConfigurer::disable)
                    .build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource()
        {
            CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
            corsConfiguration.setAllowedMethods(List.of(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(), HttpMethod.DELETE.name(), HttpMethod.PUT.name()));
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
