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
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.InstalledFileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModDirectory;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModStorage;
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
    private final ModStorage modStorage;
    private final InstalledModRepository installedModRepository;
    private final InstalledModConverter installedModConverter;
    private final SteamService steamService;
    private final ModKeyService modKeyService;
    private final ModWorkshopUrlBuilder modWorkshopUrlBuilder;
    private final InstalledModEntityHelper installedModEntityHelper;

    @Override
    @Transactional
    public void saveModFile(MultipartFile multipartFile)
    {
        if(modStorage.doesModExists(multipartFile))
            throw new ModFileAlreadyExistsException();

        try
        {
            Path savedFilePath = modStorage.save(multipartFile);
            saveModInDatabase(savedFilePath);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
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
    public List<InstalledFileSystemMod> getInstalledModsFromFileSystem()
    {
        return this.modStorage.getInstalledModsFromFileSystem();
    }

    @Override
    public ModsView getModsView()
    {
        return toModsView(getInstalledMods());
    }

    @Override
    @Transactional
    public void deleteMod(String modName)
    {
        InstalledModEntity installedModEntity = this.modStorage.getInstalledMod(modName);
        if (installedModEntity == null)
            throw new RuntimeException(format("Mod [%s] does not exist", modName));

        this.modStorage.deleteMod(installedModEntity);
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

    private void saveModInDatabase(Path modDirectoryPath)
    {
        ModDirectory modDirectory = ModDirectory.from(modDirectoryPath);
        InstalledModEntity installedModEntity = installedModRepository.findByName(modDirectory.getModName()).orElse(null);
        if (installedModEntity != null)
            return;

        installedModRepository.save(installedModEntityHelper.toEntity(InstalledFileSystemMod.from(modDirectory)));
    }

    private ModsView toModsView(List<InstalledModEntity> installedModEntities)
    {
        List<InstalledFileSystemMod> fileSystemMods = this.modStorage.getInstalledModsFromFileSystem();

        ModsView modsView = new ModsView();
        Set<ModView> disabledModViews = installedModEntities.stream()
                .filter(mod -> !mod.isEnabled())
                .map(modEntity -> ModView.builder()
                                .workshopFileId(modEntity.getWorkshopFileId())
                                .name(modEntity.getName())
                                .serverMod(modEntity.isServerMod())
                                .previewUrl(modEntity.getPreviewUrl())
                                .workshopUrl(modWorkshopUrlBuilder.buildUrlForFileId(modEntity.getWorkshopFileId()))
                                .fileExists(fileSystemMods.stream().anyMatch(mod -> mod.getWorkshopFileId() == modEntity.getWorkshopFileId()))
                                .lastUpdateDateTime(modEntity.getLastWorkshopUpdate())
                                .build())
                .collect(Collectors.toSet());

        Set<ModView> enabledModViews = installedModEntities.stream()
                .filter(InstalledModEntity::isEnabled)
                .map(modEntity -> ModView.builder()
                        .workshopFileId(modEntity.getWorkshopFileId())
                        .name(modEntity.getName())
                        .serverMod(modEntity.isServerMod())
                        .previewUrl(modEntity.getPreviewUrl())
                        .workshopUrl(modWorkshopUrlBuilder.buildUrlForFileId(modEntity.getWorkshopFileId()))
                        .fileExists(fileSystemMods.stream().anyMatch(mod -> mod.getWorkshopFileId() == modEntity.getWorkshopFileId()))
                        .lastUpdateDateTime(modEntity.getLastWorkshopUpdate())
                        .build())
                .collect(Collectors.toSet());

        modsView.setDisabledMods(disabledModViews);
        modsView.setEnabledMods(enabledModViews);
        return modsView;
    }
}
