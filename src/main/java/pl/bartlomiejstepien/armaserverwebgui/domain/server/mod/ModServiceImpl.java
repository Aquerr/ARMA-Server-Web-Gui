package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AuthenticationFacade;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.EnabledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Mod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModsCollection;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter.InstalledModConverter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter.ModWorkshopUrlBuilder;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception.ModFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception.ModIdAlreadyRegisteredException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception.ModIdCannotBeZeroException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception.NotManagedModNotFoundException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.RelatedMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.FileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModDirectory;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModFileStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserDetails;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@AllArgsConstructor
@Slf4j
public class ModServiceImpl implements ModService
{
    private final AuthenticationFacade authenticationFacade;
    private final ModFileStorage modFileStorage;
    private final InstalledModRepository installedModRepository;
    private final InstalledModConverter installedModConverter;
    private final SteamService steamService;
    private final ModKeyService modKeyService;
    private final ModWorkshopUrlBuilder modWorkshopUrlBuilder;
    private final InstalledModEntityHelper installedModEntityHelper;
    private final ModDependenciesService modDependenciesService;

    @Override
    @Transactional
    public void saveModFile(MultipartFile multipartFile, boolean overwrite)
    {
        if (!overwrite && modFileStorage.doesModFileExists(multipartFile))
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
    public void installModFromWorkshop(long fileId, String modName, boolean installDependencies)
    {
        String issuer = authenticationFacade.getCurrentUser().map(AswgUserDetails::getUsername).orElse(null);
        if (installDependencies)
        {
            Map<Long, String> dependenciesToInstall = this.modDependenciesService.getDependencies(fileId).stream()
                    .filter(relatedMod -> RelatedMod.Status.NOT_INSTALLED.equals(relatedMod.getStatus()))
                    .collect(Collectors.toMap(RelatedMod::getWorkshopFileId, RelatedMod::getName));
            steamService.scheduleWorkshopModDownload(dependenciesToInstall, true, issuer);
        }

        steamService.scheduleWorkshopModDownload(fileId, modName, true, issuer);
    }

    @Override
    public List<WorkshopModInstallationRequest> getWorkShopModInstallRequests()
    {
        return steamService.getInstallingMods();
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
    public ModsCollection getModsView()
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
    public void deleteNotManagedMod(String directoryName)
    {
        this.modFileStorage.deleteFileSystemMod(directoryName);
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
        if (fileSystemMod == null)
            throw new NotManagedModNotFoundException();

        if (fileSystemMod.getWorkshopFileId() == 0)
            throw new ModIdCannotBeZeroException();
        if (this.installedModRepository.findByWorkshopFileId(fileSystemMod.getWorkshopFileId()).isPresent())
            throw new ModIdAlreadyRegisteredException();

        Path normalizedModPath = this.modFileStorage.renameModFolderToLowerCaseWithUnderscores(fileSystemMod.getModDirectory().getPath().toAbsolutePath());
        this.modFileStorage.normalizeEachFileNameInFolderRecursively(normalizedModPath);

        this.installedModRepository.save(installedModEntityHelper.toEntity(FileSystemMod.from(normalizedModPath)));
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
        FileSystemMod fileSystemMod = FileSystemMod.from(modDirectory);
        InstalledModEntity installedModEntity = installedModRepository.findFirstByName(fileSystemMod.getName()).orElse(null);
        if (installedModEntity != null)
            return;

        if (fileSystemMod.getWorkshopFileId() == 0)
            throw new ModIdCannotBeZeroException();

        installedModEntity = installedModRepository.findByWorkshopFileId(fileSystemMod.getWorkshopFileId()).orElse(null);
        if (installedModEntity != null)
        {
            throw new ModIdAlreadyRegisteredException();
        }

        installedModRepository.save(installedModEntityHelper.toEntity(FileSystemMod.from(modDirectory)));
    }

    private ModsCollection toModsView(
            List<FileSystemMod> fileSystemMods,
            List<InstalledModEntity> installedModEntities)
    {
        List<Mod> disabledMods = installedModEntities.stream()
                .filter(mod -> !mod.isEnabled())
                .map(mod -> asModView(mod, fileSystemMods))
                .toList();

        List<Mod> enabledMods = installedModEntities.stream()
                .filter(InstalledModEntity::isEnabled)
                .map(mod -> asModView(mod, fileSystemMods))
                .toList();

        List<Mod> notManagedMods = findNotManagedMods(fileSystemMods, installedModEntities).stream()
                .map(this::asModView)
                .toList();

        return new ModsCollection(disabledMods, enabledMods, notManagedMods);
    }

    private Mod asModView(FileSystemMod fileSystemMod)
    {
        return Mod.builder()
                .workshopFileId(fileSystemMod.getWorkshopFileId())
                .name(fileSystemMod.getName())
                .workshopUrl(modWorkshopUrlBuilder.buildUrlForFileId(fileSystemMod.getWorkshopFileId()))
                .status(fileSystemMod.hasFiles() ? ModStatus.READY : ModStatus.MISSING_FILES)
                .sizeBytes(fileSystemMod.getModDirectory().getSizeBytes())
                .lastWorkshopUpdateDateTime(fileSystemMod.getLastUpdated())
                .directoryName(fileSystemMod.getModDirectory().getDirectoryName())
                .build();
    }

    private Mod asModView(InstalledModEntity modEntity, List<FileSystemMod> fileSystemMods)
    {
        FileSystemMod fileSystemMod = fileSystemMods.stream().filter(mod -> mod.getModDirectory().getDirectoryName().equals(modEntity.getModDirectoryName()))
                .findFirst()
                .orElse(null);
        List<RelatedMod> relatedMods = this.modDependenciesService.getDependencies(modEntity.getWorkshopFileId());

        return Mod.builder()
                .workshopFileId(modEntity.getWorkshopFileId())
                .name(modEntity.getName())
                .serverMod(modEntity.isServerMod())
                .previewUrl(modEntity.getPreviewUrl())
                .workshopUrl(modWorkshopUrlBuilder.buildUrlForFileId(modEntity.getWorkshopFileId()))
                .status(calculateModStatus(fileSystemMod, relatedMods))
                .sizeBytes(Optional.ofNullable(fileSystemMod)
                        .map(FileSystemMod::getModDirectory)
                        .map(ModDirectory::getSizeBytes)
                        .orElse(0L))
                .lastWorkshopUpdateDateTime(modEntity.getLastWorkshopUpdateDate())
                .lastWorkshopUpdateAttemptDateTime(modEntity.getLastWorkshopUpdateAttemptDate())
                .directoryName(modEntity.getModDirectoryName())
                .build();
    }

    private ModStatus calculateModStatus(FileSystemMod fileSystemMod, List<RelatedMod> relatedMods)
    {
        if (fileSystemMod == null || !fileSystemMod.hasFiles())
            return ModStatus.MISSING_FILES;

        if (relatedMods.stream().anyMatch(relatedMod -> relatedMod.getStatus() == RelatedMod.Status.NOT_INSTALLED))
            return ModStatus.MISSING_DEPENDENCY_MODS;

        return ModStatus.READY;
    }
}
