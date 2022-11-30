package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.mod;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface ModStorage {

    Mono<Void> save(FilePart multipartFile) throws IOException;

    boolean doesModExists(String filename);

    List<String> getInstalledModNames();

    boolean deleteMod(String modName);
}
