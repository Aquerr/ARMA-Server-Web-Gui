package pl.bartlomiejstepien.armaserverwebgui.model;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class UserProfile
{
    private String username;
    private List<String> roles;
    private OffsetDateTime createDate;
}
