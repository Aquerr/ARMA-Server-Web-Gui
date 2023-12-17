package pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ServerSecurityProperties
{
    private String serverPassword;
    private String serverAdminPassword;
    private String serverCommandPassword;
    private boolean battleEye;
    private boolean verifySignatures;
    private int allowedFilePatching;
    private List<String> allowedLoadFileExtensions;
    private List<String> adminUUIDs;
}
