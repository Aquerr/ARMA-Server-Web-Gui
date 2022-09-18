package pl.bartlomiejstepien.armaserverwebgui.storage;

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
