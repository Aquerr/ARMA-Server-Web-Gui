package pl.bartlomiejstepien.armaserverwebgui.application.auth;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserDetails;

import java.util.Collection;

public class AswgAuthentication extends AbstractAuthenticationToken
{
    private final AswgUserDetails aswgUserDetails;
    private final String jwt;

    public static AswgAuthentication authenticated(AswgUserDetails aswgUser, Collection<? extends GrantedAuthority> authorities)
    {
        return new AswgAuthentication(aswgUser, authorities);
    }

    public static AswgAuthentication unauthenticated(String jwt)
    {
        return new AswgAuthentication(jwt);
    }

    public AswgAuthentication(String jwt)
    {
        super((Collection<? extends GrantedAuthority>) null);
        this.aswgUserDetails = null;
        this.jwt = jwt;
    }

    private AswgAuthentication(AswgUserDetails aswgUserDetails, @Nullable Collection<? extends GrantedAuthority> authorities)
    {
        super(authorities);
        this.aswgUserDetails = aswgUserDetails;
        this.jwt = null;
        setAuthenticated(true);
    }

    @Override
    public @Nullable String getCredentials()
    {
        return this.jwt;
    }

    @Override
    public @Nullable AswgUserDetails getPrincipal()
    {
        return this.aswgUserDetails;
    }
}
