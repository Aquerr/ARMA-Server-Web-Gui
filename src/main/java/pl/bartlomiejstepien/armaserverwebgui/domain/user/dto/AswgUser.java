package pl.bartlomiejstepien.armaserverwebgui.domain.user.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@SuperBuilder(toBuilder = true)
public class AswgUser implements Principal
{
    private Integer id;
    private String username;
    private Set<AswgAuthority> authorities;
    private OffsetDateTime createdDate;
    private boolean locked;
    private OffsetDateTime lastLoginDate;

    @Override
    public String getName()
    {
        return this.username;
    }
}
