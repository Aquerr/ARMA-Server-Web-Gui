package pl.bartlomiejstepien.armaserverwebgui.application.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.model.InvalidJwtTokenEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.interfaces.jwt.InvalidJwtTokenRepository;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Optional.ofNullable;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService
{
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private static final SecretKey KEY = Jwts.SIG.HS256.key().build();
    private static final Map<String, BlackListedJwt> BLACK_LISTED_JWTS = new HashMap<>();
    private static final ScheduledExecutorService BLACK_LISTED_JWTS_CLEARER_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    @Value("${aswg.security.jwt.issuer}")
    private String jwtIssuer;

    @Value("${aswg.security.jwt.expiration-time}")
    private Duration jwtExpirationTime;

    static {
        BLACK_LISTED_JWTS_CLEARER_EXECUTOR_SERVICE.scheduleAtFixedRate(JwtService::clearBlacklistedJwts, 1, 1, TimeUnit.HOURS);
    }

    private static void clearBlacklistedJwts()
    {
        for (final BlackListedJwt blackListedJwt : List.copyOf(BLACK_LISTED_JWTS.values()))
        {
            if (ZonedDateTime.now().isAfter(blackListedJwt.expirationTime()))
            {
                BLACK_LISTED_JWTS.remove(blackListedJwt.jwt());
            }
        }
    }

    private final InvalidJwtTokenRepository invalidJwtTokenRepository;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event)
    {
        invalidJwtTokenRepository.findAll()
                .map(invalidJwtTokenEntity -> new BlackListedJwt(invalidJwtTokenEntity.getJwt(), invalidJwtTokenEntity.getExpirationDateTime()))
                .toIterable()
                .forEach(blackListedJwt -> BLACK_LISTED_JWTS.put(blackListedJwt.jwt(), blackListedJwt));
    }

    public String createJwt(AswgUser aswgUser)
    {
        return Jwts.builder()
                .subject(aswgUser.getUsername())
                .signWith(KEY)
                .issuer(jwtIssuer)
                .compressWith(Jwts.ZIP.DEF)
                .expiration(Date.from(Instant.now().plus(jwtExpirationTime)))
                .issuedAt(Date.from(Instant.now()))
                .compact();
    }

    public Jws<Claims> validateJwt(String jwt)
    {
        try
        {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .requireIssuer(jwtIssuer)
                    .build()
                    .parseSignedClaims(jwt);
        }
        catch (Exception exception)
        {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    public String extractJwt(ServerWebExchange serverWebExchange)
    {
        return ofNullable(serverWebExchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER))
                .filter(headerValue -> headerValue.startsWith(BEARER))
                .map(headerValue -> headerValue.substring(BEARER.length()))
                .orElse(null);
    }

    public Mono<Void> invalidate(String jwt)
    {
        return Mono.just(validateJwt(jwt))
                .map(jws -> jws.getPayload().getExpiration().toInstant().atZone(ZoneId.systemDefault()))
                .flatMap(expirationDateTime -> {
                    BLACK_LISTED_JWTS.put(jwt, new BlackListedJwt(jwt, expirationDateTime));

                    InvalidJwtTokenEntity invalidJwtTokenEntity = new InvalidJwtTokenEntity();
                    invalidJwtTokenEntity.setJwt(jwt);
                    invalidJwtTokenEntity.setInvalidatedDateTime(ZonedDateTime.now());
                    invalidJwtTokenEntity.setExpirationDateTime(expirationDateTime);
                    return invalidJwtTokenRepository.save(invalidJwtTokenEntity);
                })
                .doOnError((throwable -> log.error("Could not save invalid jwt '{}'", jwt, throwable)))
                .onErrorResume(throwable -> Mono.empty())
                .then();
    }

    private record BlackListedJwt(String jwt, ZonedDateTime expirationTime) { }
}
