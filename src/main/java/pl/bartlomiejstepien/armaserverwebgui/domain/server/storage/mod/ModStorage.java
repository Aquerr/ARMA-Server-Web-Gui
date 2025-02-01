package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.exception.CouldNotReadModMetaFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ModStorage {

    Path save(MultipartFile multipartFile) throws IOException;

    boolean doesModExists(MultipartFile multipartFile);

    List<InstalledFileSystemMod> getInstalledModsFromFileSystem();

    MetaCppFile readModMetaFile(Path modDirectory) throws CouldNotReadModMetaFile;

    ModCppFile readModFile(Path modDirectory) throws CouldNotReadModMetaFile;

    void deleteMod(InstalledModEntity installedModEntity);

    InstalledModEntity getInstalledMod(String modName);

    Path copyModFolderFromSteamCmd(Path steamCmdModFolderPath, ModDirectory modDirectory);

    Path linkModFolderToSteamCmdModFolder(Path steamCmdModFolderPath, ModDirectory modDirectory);
}
