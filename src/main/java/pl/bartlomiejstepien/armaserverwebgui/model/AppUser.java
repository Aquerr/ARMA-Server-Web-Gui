package pl.bartlomiejstepien.armaserverwebgui.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AppUser
{
    private String username;
    private String password;
    private List<String> roles;
    private OffsetDateTime createdDate;
}
