package pl.bartlomiejstepien.armaserverwebgui.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerSecurity
{
    private String serverPassword;
    private String serverAdminPassword;
    private String serverCommandPassword;
}
