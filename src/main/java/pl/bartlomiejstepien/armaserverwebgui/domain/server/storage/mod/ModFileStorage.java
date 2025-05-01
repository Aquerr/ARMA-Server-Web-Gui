package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.exception.CouldNotReadModMetaFile;

public interface ModFileStorage
{

    Path save(MultipartFile multipartFile) throws IOException;

    boolean doesModFileExists(MultipartFile multipartFile);

    boolean doesModFileExists(String modName);

    List<FileSystemMod> getModsFromFileSystem();

    MetaCppFile readModMetaFile(Path modDirectory) throws CouldNotReadModMetaFile;

    ModCppFile readModFile(Path modDirectory) throws CouldNotReadModMetaFile;

    void deleteFileSystemMod(String modName);

    void deleteMod(InstalledModEntity installedModEntity);

    InstalledModEntity getInstalledMod(String modName);

    Path copyModFolderFromSteamCmd(Path steamCmdModFolderPath, ModDirectory modDirectory);

    Path linkModFolderToSteamCmdModFolder(Path steamCmdModFolderPath, ModDirectory modDirectory);

    void normalizeEachFileNameInFolderRecursively(Path filePath);

    Path renameModFolderToLowerCaseWithUnderscores(Path modFolderPath);
}
