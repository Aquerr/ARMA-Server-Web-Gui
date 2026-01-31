package pl.bartlomiejstepien.armaserverwebgui.domain.user.dto;

import lombok.Builder;
import lombok.Value;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;

import java.security.Principal;
import java.util.Set;

@Builder
@Value
public class AswgUserDetails implements Principal
{
    String username;
    Set<AswgAuthority> authorities;

    @Override
    public String getName()
    {
        return this.username;
    }
}
