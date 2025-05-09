package pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

@Data
@Builder
public class ServerSecurityProperties
{
    private String serverPassword;
    private String serverAdminPassword;
    private String serverCommandPassword;
    private boolean battleEye;
    private boolean verifySignatures;
    private AllowedFilePatching allowedFilePatching;
    private List<String> filePatchingIgnoredClients;
    private List<String> allowedLoadFileExtensions;
    private List<String> adminUUIDs;
    private List<VoteCommand> voteCommands;
    private boolean kickDuplicate;
    private String voteThreshold;
    private int voteMissionPlayers;

    @Getter
    public enum AllowedFilePatching
    {
        NOT_ALLOWED("NOT_ALLOWED", 0),
        ALLOWED_FOR_HEADLESS_CLIENTS("ALLOWED_FOR_HEADLESS_CLIENTS", 1),
        ALLOWED_FOR_ALL_CLIENTS("ALLOWED_FOR_ALL_CLIENTS", 2);

        @JsonValue
        private final String name;

        private final int configValue;

        AllowedFilePatching(String name, int configValue)
        {
            this.name = name;
            this.configValue = configValue;
        }

        public static AllowedFilePatching findByConfigValue(int configValue)
        {
            return Stream.of(values())
                    .filter(filePatching -> filePatching.getConfigValue() == configValue)
                    .findFirst()
                    .orElse(null);
        }
    }
}
