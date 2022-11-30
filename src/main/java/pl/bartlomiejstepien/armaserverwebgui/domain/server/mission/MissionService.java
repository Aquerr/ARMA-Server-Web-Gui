package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import org.springframework.http.codec.multipart.FilePart;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Missions;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MissionService
{
    Mono<Void> save(FilePart multipartFile);

    List<String> getInstalledMissionNames();

    boolean deleteMission(String missionName);

    void saveEnabledMissionList(List<String> missions);

    Missions getMissions();
}
