package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import org.springframework.http.codec.multipart.FilePart;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.exception.CouldNotReadModMetaFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ModStorage {

    Mono<Path> save(FilePart multipartFile) throws IOException;

    boolean doesModExists(FilePart filename);

    List<InstalledFileSystemMod> getInstalledModsFromFileSystem();

    ModMetaFile readModMetaFile(Path modDirectory) throws CouldNotReadModMetaFile;

    Mono<Boolean> deleteMod(InstalledModEntity installedModEntity);

    Mono<InstalledModEntity> getInstalledMod(String modName);

    Path copyModFolderFromSteamCmd(Path steamCmdModFolderPath, Path armaServerDir, String modName);
}
