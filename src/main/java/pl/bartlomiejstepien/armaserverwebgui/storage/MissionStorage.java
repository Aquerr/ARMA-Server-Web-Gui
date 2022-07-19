package pl.bartlomiejstepien.armaserverwebgui.storage;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface MissionStorage
{
    Mono<Void> save(FilePart multipartFile) throws IOException;

    boolean doesMissionExists(String filename);

    List<String> getInstalledMissionNames();
}
