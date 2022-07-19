package pl.bartlomiejstepien.armaserverwebgui.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MissionService
{
    Mono<Void> save(FilePart multipartFile);

    List<String> getInstalledMissionNames();

    boolean deleteMission(String missionName);
}
