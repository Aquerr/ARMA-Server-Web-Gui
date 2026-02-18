package pl.bartlomiejstepien.armaserverwebgui.domain.user.dto;

import lombok.Builder;
import lombok.Value;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;

import java.util.Set;

@Value
@Builder
public class UserUpdateCommand
{
    Integer userId;
    boolean locked;
    Set<AswgAuthority> authorities;
}
