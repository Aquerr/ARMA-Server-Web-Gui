package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MissionService
{
    Mono<Void> save(FilePart multipartFile);

    List<String> getInstalledMissionNames();

    boolean deleteMission(String missionName);

    void saveEnabledMissionList(List<Mission> missions);

    Missions getMissions();

    Mono<ResponseEntity<?>> addMission(String template);
}
