package pl.bartlomiejstepien.armaserverwebgui.web.response;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class UserProfileResponse
{
    private String username;
    private Set<String> authorities;
    private OffsetDateTime createDate;
}
