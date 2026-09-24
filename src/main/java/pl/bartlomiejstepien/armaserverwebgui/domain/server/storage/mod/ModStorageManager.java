package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModStorageManager
{
    private final ModFileStorage modFileStorage;
    private final InstalledModRepository installedModRepository;

    @Transactional
    public void deleteMod(InstalledModEntity installedModEntity)
    {
        this.modFileStorage.deleteMod(ModDirectory.from(Paths.get(installedModEntity.getDirectoryPath())));
        this.installedModRepository.delete(installedModEntity);
    }

    @Transactional
    public void deleteFromDbOnly(long id)
    {
        this.installedModRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public InstalledModEntity getInstalledMod(String modName)
    {
        return this.installedModRepository.findFirstByName(modName).orElse(null);
    }

    public List<FileSystemMod> getModsFromFileSystem()
    {
        return this.modFileStorage.getModsFromFileSystem();
    }

    public Path copyModFolderFromSteamCmd(Path steamCmdModFolderPath, ModDirectory modDirectory)
    {
        return this.modFileStorage.copyModFolderFromSteamCmd(steamCmdModFolderPath, modDirectory);
    }

    public Path linkModFolderToSteamCmdModFolder(Path steamCmdModFolderPath, ModDirectory modDirectory)
    {
        return this.modFileStorage.linkModFolderToSteamCmdModFolder(steamCmdModFolderPath, modDirectory);
    }

    public void normalizeEachFileNameInFolderRecursively(Path filePath)
    {
        this.modFileStorage.normalizeEachFileNameInFolderRecursively(filePath);
    }

    public Path renameModFolderToLowerCaseWithUnderscores(Path modFolderPath)
    {
        return this.modFileStorage.renameModFolderToLowerCaseWithUnderscores(modFolderPath);
    }

    public boolean doesModFileExists(String modName)
    {
        return this.modFileStorage.doesModFileExists(modName);
    }

    public Path save(MultipartFile multipartFile) throws IOException
    {
        return this.modFileStorage.save(multipartFile);
    }

    public void deleteFileSystemMod(String directoryName)
    {
        this.modFileStorage.deleteFileSystemMod(directoryName);
    }
}
