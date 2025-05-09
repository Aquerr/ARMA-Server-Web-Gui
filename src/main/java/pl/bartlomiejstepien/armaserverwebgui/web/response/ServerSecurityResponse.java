package pl.bartlomiejstepien.armaserverwebgui.web.response;

import lombok.Builder;
import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model.ServerSecurityProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model.VoteCommand;

import java.util.List;

@Data
@Builder
public class ServerSecurityResponse
{
    private String serverPassword;
    private String serverAdminPassword;
    private String serverCommandPassword;
    private boolean battleEye;
    private boolean verifySignatures;
    private ServerSecurityProperties.AllowedFilePatching allowedFilePatching;
    private List<String> filePatchingIgnoredClients;
    private List<String> allowedLoadFileExtensions;
    private List<String> adminUUIDs;
    private List<VoteCommand> allowedVoteCmds;
    private boolean kickDuplicate;
    private String voteThreshold;
    private int voteMissionPlayers;
}