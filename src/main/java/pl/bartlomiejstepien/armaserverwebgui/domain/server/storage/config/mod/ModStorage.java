package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.mod;

import org.springframework.http.codec.multipart.FilePart;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.InstalledMod;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ModStorage {

    Mono<Path> save(FilePart multipartFile) throws IOException;

    boolean doesModExists(String filename);

    List<InstalledMod> getInstalledModsFromFileSystem();

    ModMetaFile readModMetaFile(Path modDirectory);

    Mono<Boolean> deleteMod(InstalledMod installedMod);

    Mono<InstalledMod> getInstalledMod(String modName);
}
