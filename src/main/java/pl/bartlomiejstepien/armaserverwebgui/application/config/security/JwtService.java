package pl.bartlomiejstepien.armaserverwebgui.application.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class JwtService
{
    private static final SecretKey KEY = Jwts.SIG.HS256.key().build();

    @Value("${aswg.security.jwt.issuer}")
    private String jwtIssuer;

    @Value("${aswg.security.jwt.expiration-time}")
    private Duration jwtExpirationTime;

    public String createJwt(String username)
    {
        return Jwts.builder()
                .subject(username)
                .signWith(KEY)
                .issuer(jwtIssuer)
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
}
