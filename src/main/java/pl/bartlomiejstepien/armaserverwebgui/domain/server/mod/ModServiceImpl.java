package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.EnabledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModView;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter.InstalledModConverter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter.ModWorkshopUrlBuilder;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception.ModFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception.NotManagedModNotFoundException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.FileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModDirectory;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModFileStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@AllArgsConstructor
@Slf4j
public class ModServiceImpl implements ModService
{
    private final ModFileStorage modFileStorage;
    private final InstalledModRepository installedModRepository;
    private final InstalledModConverter installedModConverter;
    private final SteamService steamService;
    private final ModKeyService modKeyService;
    private final ModWorkshopUrlBuilder modWorkshopUrlBuilder;
    private final InstalledModEntityHelper installedModEntityHelper;

    @Override
    @Transactional
    public void saveModFile(MultipartFile multipartFile, boolean overwrite)
    {
        if(!overwrite && modFileStorage.doesModFileExists(multipartFile))
            throw new ModFileAlreadyExistsException();

        try
        {
            Path savedFilePath = modFileStorage.save(multipartFile);
            saveModInDatabase(savedFilePath);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkModFileExists(String modName)
    {
        return modFileStorage.doesModFileExists(modName);
    }

    @Override
    public void installModFromWorkshop(long fileId, String modName)
    {
        steamService.scheduleWorkshopModDownload(fileId, modName, true);
    }

    @Override
    public List<WorkshopModInstallationRequest> getWorkShopModInstallRequests()
    {
        return steamService.getInstallingMods();
    }

    @Transactional
    @Override
    public InstalledModEntity saveToDB(InstalledModEntity installedModEntity)
    {
        return this.installedModRepository.save(installedModEntity);
    }

    @Transactional
    @Override
    public void deleteFromDB(long id)
    {
        this.installedModRepository.deleteById(id);
    }

    @Override
    public List<FileSystemMod> getInstalledModsFromFileSystem()
    {
        return this.modFileStorage.getModsFromFileSystem();
    }

    @Override
    public ModsView getModsView()
    {
        return toModsView(getInstalledModsFromFileSystem(), getInstalledMods());
    }

    @Override
    @Transactional
    public void deleteMod(String modName)
    {
        InstalledModEntity installedModEntity = this.modFileStorage.getInstalledMod(modName);
        if (installedModEntity == null)
            throw new RuntimeException(format("Mod [%s] does not exist", modName));

        this.modFileStorage.deleteMod(installedModEntity);
    }

    @Override
    @Transactional
    public void saveEnabledModList(Set<EnabledMod> enabledMods)
    {
        List<InstalledModEntity> installedModEntities = installedModRepository.findAll();
        Set<InstalledModEntity> modsToActivate = enabledMods.stream()
                .map(modView -> installedModEntities.stream()
                    .filter(mod -> mod.getWorkshopFileId() == (modView.getWorkshopFileId()))
                    .findFirst()
                    .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        installedModRepository.disableAllMods();
        installedModRepository.enableMods(modsToActivate.stream()
                .map(InstalledModEntity::getWorkshopFileId)
                .toList());
        installedModRepository.setServerMods(modsToActivate.stream()
                .filter(InstalledModEntity::isServerMod)
                .map(InstalledModEntity::getWorkshopFileId)
                .toList());
        modKeyService.clearServerKeys();

        modsToActivate.stream()
                        .map(InstalledModEntity::getDirectoryPath)
                        .map(Paths::get)
                        .map(ModDirectory::from)
                        .forEach(modKeyService::copyKeysForMod);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstalledModEntity> getInstalledMods()
    {
        return this.installedModRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkshopMod> getInstalledWorkshopMods()
    {
        return this.installedModRepository.findAllByOrderByNameAsc().stream()
                .map(installedModConverter::convertToWorkshopMod)
                .toList();
    }

    @Override
    @Transactional
    public void manageMod(String name)
    {
        List<FileSystemMod> notManagedMods = findNotManagedMods();
        FileSystemMod fileSystemMod = notManagedMods.stream()
                .filter(mod -> mod.getName().equals(name))
                .findFirst()
                .orElse(null);

        if (fileSystemMod == null)
            throw new NotManagedModNotFoundException();

        startManagingMod(fileSystemMod);
    }

    private void startManagingMod(FileSystemMod fileSystemMod)
    {
        Path normalizedModPath = this.modFileStorage.renameModFolderToLowerCaseWithUnderscores(fileSystemMod.getModDirectory().getPath());
        this.modFileStorage.normalizeEachFileNameInFolderRecursively(normalizedModPath);

        FileSystemMod correctedFileSystemMod = findNotManagedMods().stream()
                .filter(mod -> mod.getName().equals(fileSystemMod.getName()))
                .findFirst()
                .orElse(null);

        if (correctedFileSystemMod == null)
            throw new NotManagedModNotFoundException();

        saveToDB(installedModEntityHelper.toEntity(correctedFileSystemMod));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileSystemMod> findNotManagedMods()
    {
        return this.findNotManagedMods(this.getInstalledModsFromFileSystem(), this.getInstalledMods());
    }

    private List<FileSystemMod> findNotManagedMods(List<FileSystemMod> fileSystemMods, List<InstalledModEntity> installedModEntities)
    {
        return fileSystemMods.stream()
                .filter(fileSystemMod -> installedModEntities.stream()
                        .noneMatch(installedModEntity -> installedModEntity.getModDirectoryName().equals(fileSystemMod.getModDirectory().getDirectoryName())))
                .toList();

    }

    private void saveModInDatabase(Path modDirectoryPath)
    {
        ModDirectory modDirectory = ModDirectory.from(modDirectoryPath);
        InstalledModEntity installedModEntity = installedModRepository.findByName(modDirectory.getModName()).orElse(null);
        if (installedModEntity != null)
            return;

        installedModRepository.save(installedModEntityHelper.toEntity(FileSystemMod.from(modDirectory)));
    }

    private ModsView toModsView(
            List<FileSystemMod> fileSystemMods,
            List<InstalledModEntity> installedModEntities)
    {
        ModsView modsView = new ModsView();
        List<ModView> disabledModViews = installedModEntities.stream()
                .filter(mod -> !mod.isEnabled())
                .map(mod -> asModView(mod, fileSystemMods))
                .toList();

        List<ModView> enabledModViews = installedModEntities.stream()
                .filter(InstalledModEntity::isEnabled)
                .map(mod -> asModView(mod, fileSystemMods))
                .toList();

        List<ModView> notManagedMods = findNotManagedMods(fileSystemMods, installedModEntities).stream()
                .map(this::asModView)
                .toList();

        modsView.setDisabledMods(disabledModViews);
        modsView.setEnabledMods(enabledModViews);
        modsView.setNotManagedMods(notManagedMods);
        return modsView;
    }

    private ModView asModView(FileSystemMod fileSystemMod)
    {
        return ModView.builder()
                .workshopFileId(fileSystemMod.getWorkshopFileId())
                .name(fileSystemMod.getName())
                .workshopUrl(modWorkshopUrlBuilder.buildUrlForFileId(fileSystemMod.getWorkshopFileId()))
                .fileExists(fileSystemMod.isValid())
                .sizeBytes(fileSystemMod.getModDirectory().getSizeBytes())
                .lastUpdateDateTime(fileSystemMod.getLastUpdated())
                .build();
    }

    private ModView asModView(InstalledModEntity modEntity, List<FileSystemMod> fileSystemMods)
    {
        return ModView.builder()
                .workshopFileId(modEntity.getWorkshopFileId())
                .name(modEntity.getName())
                .serverMod(modEntity.isServerMod())
                .previewUrl(modEntity.getPreviewUrl())
                .workshopUrl(modWorkshopUrlBuilder.buildUrlForFileId(modEntity.getWorkshopFileId()))
                .fileExists(fileSystemMods.stream().anyMatch(mod -> mod.getWorkshopFileId() == modEntity.getWorkshopFileId()))
                .sizeBytes(fileSystemMods.stream().filter(mod -> mod.getModDirectory().getDirectoryName().equals(modEntity.getModDirectoryName()))
                        .findFirst()
                        .map(mod -> mod.getModDirectory().getSizeBytes())
                        .orElse(0L))
                .lastUpdateDateTime(modEntity.getLastWorkshopUpdate())
                .build();
    }
}
