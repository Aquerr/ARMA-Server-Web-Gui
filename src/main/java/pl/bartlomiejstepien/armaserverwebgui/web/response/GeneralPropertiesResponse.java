package pl.bartlomiejstepien.armaserverwebgui.web.response;

import lombok.Builder;
import lombok.Value;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ArmaServerParameters;

import java.util.List;

@Value(staticConstructor = "of")
@Builder
public class GeneralPropertiesResponse
{
    String serverDirectory;
    String modsDirectory;
    String commandLineParams;
    String hostname;
    int port;
    int maxPlayers;
    List<String> motd;
    int motdInterval;
    boolean persistent;
    boolean drawingInMap;
    List<String> headlessClients;
    List<String> localClients;
    Mission.Difficulty forcedDifficulty;

    public static GeneralPropertiesResponse of(String serverDirectory,
                                               String modsDirectory,
                                               Integer port,
                                               ArmaServerParameters armaServerParameters,
                                               GeneralProperties generalProperties)
    {
        return GeneralPropertiesResponse.builder()
                .serverDirectory(serverDirectory)
                .modsDirectory(modsDirectory)
                .commandLineParams(armaServerParameters.asString())
                .port(port)
                .hostname(generalProperties.getHostname())
                .maxPlayers(generalProperties.getMaxPlayers())
                .motd(generalProperties.getMotd())
                .motdInterval(generalProperties.getMotdInterval())
                .persistent(generalProperties.isPersistent())
                .drawingInMap(generalProperties.isDrawingInMap())
                .headlessClients(generalProperties.getHeadlessClients())
                .localClients(generalProperties.getLocalClients())
                .forcedDifficulty(generalProperties.getForcedDifficulty())
                .build();
    }
}
