package pl.bartlomiejstepien.armaserverwebgui.web.response;

import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Data;

@Data
public class UserProfileResponse
{
    private String username;
    private Set<String> authorities;
    private OffsetDateTime createDate;
}
