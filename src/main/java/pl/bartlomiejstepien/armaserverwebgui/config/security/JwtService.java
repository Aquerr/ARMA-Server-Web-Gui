package pl.bartlomiejstepien.armaserverwebgui.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService
{
    //TODO: Move to application.properties
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final String KEY_ISSUER = "Arma-Server-Web-Gui";

    public String createJwt(String username)
    {
        return Jwts.builder()
                .setSubject(username)
                .signWith(KEY)
                .setIssuer(KEY_ISSUER)
                .setExpiration(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .setIssuedAt(Date.from(Instant.now()))
                .compact();
    }

    public Jws<Claims> validateJwt(String jwt)
    {
        try
        {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(jwt);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            return null;
        }
    }
}
