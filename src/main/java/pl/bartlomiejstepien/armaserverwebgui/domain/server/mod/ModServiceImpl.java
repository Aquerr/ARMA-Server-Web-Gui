package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.EnabledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModView;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter.InstalledModConverter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter.ModWorkshopUrlBuilder;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception.ModFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.exception.CouldNotReadModMetaFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.InstalledFileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModMetaFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ModServiceImpl implements ModService
{
    private final ModStorage modStorage;
    private final InstalledModRepository installedModRepository;
    private final InstalledModConverter installedModConverter;
    private final SteamService steamService;
    private final ModKeyService modKeyService;
    private final ModWorkshopUrlBuilder modWorkshopUrlBuilder;

    @Override
    public Mono<InstalledModEntity> saveModFile(FilePart multipartFile)
    {
        if(modStorage.doesModExists(multipartFile))
            throw new ModFileAlreadyExistsException();

        try
        {
            return modStorage.save(multipartFile).flatMap(this::saveModInDatabase);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<Void> installModFromWorkshop(long fileId, String modName)
    {
        steamService.scheduleWorkshopModDownload(fileId, modName);
        return Mono.empty();
    }

    @Override
    public List<WorkshopModInstallationRequest> getWorkShopModInstallRequests()
    {
        return steamService.getInstallingMods();
    }

    @Transactional
    @Override
    public Mono<InstalledModEntity> saveToDB(InstalledModEntity installedModEntity)
    {
        return this.installedModRepository.save(installedModEntity);
    }

    @Transactional
    @Override
    public Mono<Void> deleteFromDB(long id)
    {
        return this.installedModRepository.deleteById(id);
    }

    @Override
    public List<InstalledFileSystemMod> getInstalledModsFromFileSystem()
    {
        return this.modStorage.getInstalledModsFromFileSystem();
    }

    @Override
    public Mono<ModsView> getModsView()
    {
        return getInstalledMods().collectList().map(this::toModsView);
    }

    @Override
    public Mono<Boolean> deleteMod(String modName)
    {
        return this.modStorage.getInstalledMod(modName)
                .flatMap(this.modStorage::deleteMod);
    }

    @Override
    public void saveEnabledModList(Set<EnabledMod> enabledMods)
    {
        List<InstalledFileSystemMod> installedModEntities = getInstalledModsFromFileSystem();
        Set<InstalledFileSystemMod> installedActiveMods = enabledMods.stream()
                .map(modView -> installedModEntities.stream()
                    .filter(mod -> mod.getWorkshopFileId() == (modView.getWorkshopFileId()))
                    .findFirst()
                    .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        installedModRepository.disableAllMods().subscribe();
        installedModRepository.enableMods(enabledMods.stream()
                .map(EnabledMod::getWorkshopFileId)
                .toList()).subscribe();
        installedModRepository.setServerMods(enabledMods.stream()
                .filter(EnabledMod::getServerMod)
                .map(EnabledMod::getWorkshopFileId)
                .toList()).subscribe();

        modKeyService.clearServerKeys();
        installedActiveMods.forEach(modKeyService::copyKeysForMod);
    }

    @Override
    public Flux<InstalledModEntity> getInstalledMods()
    {
        return this.installedModRepository.findAll();
    }

    @Override
    public Flux<ArmaWorkshopMod> getInstalledWorkshopMods()
    {
        return this.installedModRepository.findAllByOrderByNameAsc()
                .map(installedModConverter::convertToWorkshopMod);
    }

    private Mono<InstalledModEntity> saveModInDatabase(Path modDirectory)
    {
        ModMetaFile modMetaFile = null;
        try
        {
            modMetaFile = modStorage.readModMetaFile(modDirectory);
        }
        catch (CouldNotReadModMetaFile e)
        {
            throw new RuntimeException(e);
        }

        InstalledModEntity.InstalledModEntityBuilder installedModBuilder = InstalledModEntity.builder();
        installedModBuilder.workshopFileId(modMetaFile.getPublishedFileId());
        installedModBuilder.name(modMetaFile.getName());
        installedModBuilder.directoryPath(modDirectory.toAbsolutePath().toString());
        installedModBuilder.createdDate(OffsetDateTime.now());

        ArmaWorkshopMod armaWorkshopMod = steamService.getWorkshopMod(modMetaFile.getPublishedFileId());
        if (armaWorkshopMod != null)
        {
            installedModBuilder.previewUrl(armaWorkshopMod.getPreviewUrl());
        }
        InstalledModEntity installedModEntity = installedModBuilder.build();

        return installedModRepository.save(installedModEntity);
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
                        .build())
                .collect(Collectors.toSet());

        modsView.setDisabledMods(disabledModViews);
        modsView.setEnabledMods(enabledModViews);
        return modsView;
    }
}
