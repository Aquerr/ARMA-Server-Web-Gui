package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import org.springframework.http.codec.multipart.FilePart;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Missions;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MissionService
{
    Mono<Void> save(FilePart multipartFile);

    Mono<Boolean> deleteMission(String template);

    Mono<Void> saveEnabledMissionList(List<Mission> missions);

    Mono<Missions> getMissions();

    Mono<Void> addMission(String name, String template);

    Mono<Void> updateMission(long id, Mission mission);
}
