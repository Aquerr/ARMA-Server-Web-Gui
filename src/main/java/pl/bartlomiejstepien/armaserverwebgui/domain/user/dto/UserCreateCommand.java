package pl.bartlomiejstepien.armaserverwebgui.domain.user.dto;

import lombok.Builder;
import lombok.Value;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;

import java.time.OffsetDateTime;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class UserCreateCommand
{
    Integer id;
    String username;
    String password;
    Set<AswgAuthority> authorities;
    OffsetDateTime createdDate;
    boolean locked;
    OffsetDateTime lastLoginDate;
}
