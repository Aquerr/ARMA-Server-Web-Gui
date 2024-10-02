package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface MissionFileStorage
{
    Mono<Void> save(FilePart multipartFile) throws IOException;

    boolean doesMissionExists(String filename);

    List<String> getInstalledMissionTemplates();

    boolean deleteMission(String template);
}
