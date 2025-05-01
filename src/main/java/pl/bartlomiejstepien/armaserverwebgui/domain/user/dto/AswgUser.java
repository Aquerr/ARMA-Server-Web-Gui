package pl.bartlomiejstepien.armaserverwebgui.domain.user.dto;

import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;

@Data
@SuperBuilder(toBuilder = true)
public class AswgUser
{
    private Integer id;
    private String username;
    private Set<AswgAuthority> authorities;
    private OffsetDateTime createdDate;
    private boolean locked;
}
