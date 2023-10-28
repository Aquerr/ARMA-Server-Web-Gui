package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModDir;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModView;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter.InstalledModConverter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter.ModWorkshopUrlBuilder;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception.ModFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
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
import java.util.HashSet;
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
    private final ASWGConfig aswgConfig;
    private final InstalledModConverter installedModConverter;
    private final SteamService steamService;
    private final WorkShopModInstallService workShopModInstallService;
    private final ModKeyService modKeyService;
    private final ModWorkshopUrlBuilder modWorkshopUrlBuilder;

    @Override
    public Mono<InstalledMod> saveModFile(FilePart multipartFile)
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
    public Mono<InstalledMod> installModFromWorkshop(long fileId, String modName)
    {
        workShopModInstallService.queueWorkshopModInstallation(new WorkshopModInstallationRequest(fileId, modName));
        return Mono.empty();
    }

    @Override
    public List<WorkshopModInstallationRequest> getWorkShopModInstallRequests()
    {
        return workShopModInstallService.getWorkShopModInstallRequests();
    }

    @Transactional
    @Override
    public Mono<InstalledMod> saveToDB(InstalledMod installedMod)
    {
        return this.installedModRepository.save(installedMod);
    }

    @Transactional
    @Override
    public Mono<Void> deleteFromDB(long id)
    {
        return this.installedModRepository.deleteById(id);
    }

    @Override
    public List<InstalledMod> getInstalledModsFromFileSystem()
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
                .doOnSuccess(installedMod -> {
                    Set<ModDir> enabledModDirs = this.aswgConfig.getActiveModDirs();
                    enabledModDirs.removeIf(mod -> mod.getDirName().equals(installedMod.getModDirectoryName()));
                    this.aswgConfig.setActiveModDirs(enabledModDirs);
                })
                .flatMap(this.modStorage::deleteMod);
    }

    @Override
    public void saveEnabledModList(Set<ModView> mods)
    {
        List<InstalledMod> installedMods = getInstalledModsFromFileSystem();
        Set<InstalledMod> installedActiveMods = mods.stream()
                .map(modView -> installedMods.stream()
                    .filter(mod -> mod.getName().equals(modView.getName()))
                    .findFirst()
                    .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<ModDir> activeModDirs = installedActiveMods.stream()
                .map(installedMod -> new ModDir(installedMod.getModDirectoryName(), mods.stream()
                        .filter(modView -> modView.getName().equals(installedMod.getName()))
                        .findFirst()
                        .map(ModView::isServerMod)
                        .orElse(false))
                )
                .collect(Collectors.toSet());

        this.aswgConfig.setActiveModDirs(activeModDirs);
        modKeyService.clearServerKeys();
        installedActiveMods.forEach(modKeyService::copyKeysForMod);
    }

    @Override
    public Flux<InstalledMod> getInstalledMods()
    {
        return this.installedModRepository.findAll();
    }

    @Override
    public Flux<ArmaWorkshopMod> getInstalledWorkshopMods()
    {
        return this.installedModRepository.findAllByOrderByNameAsc()
                .map(installedModConverter::convertToWorkshopMod);
    }

    private Mono<InstalledMod> saveModInDatabase(Path modDirectory)
    {
        ModMetaFile modMetaFile = modStorage.readModMetaFile(modDirectory);

        InstalledMod.InstalledModBuilder installedModBuilder = InstalledMod.builder();
        installedModBuilder.publishedFileId(modMetaFile.getPublishedFileId());
        installedModBuilder.name(modMetaFile.getName());
        installedModBuilder.directoryPath(modDirectory.toAbsolutePath().toString());
        installedModBuilder.createdDate(OffsetDateTime.now());

        ArmaWorkshopMod armaWorkshopMod = steamService.getWorkshopMod(modMetaFile.getPublishedFileId());
        if (armaWorkshopMod != null)
        {
            installedModBuilder.previewUrl(armaWorkshopMod.getPreviewUrl());
        }
        InstalledMod installedMod = installedModBuilder.build();

        return installedModRepository.save(installedMod);
    }

    private ModsView toModsView(List<InstalledMod> installedMods)
    {
        Set<ModDir> enabledModDirs = this.aswgConfig.getActiveModDirs();

        ModsView modsView = new ModsView();
        Set<ModView> disabledModViews = installedMods.stream()
                .filter(installedMod -> enabledModDirs.stream().noneMatch(modDir -> installedMod.getModDirectoryName().equals(modDir.getDirName())))
                .map(installedMod -> new ModView(installedMod.getName(), false, installedMod.getPreviewUrl(), modWorkshopUrlBuilder.buildUrlForFileId(installedMod.getPublishedFileId())))
                .collect(Collectors.toSet());

        Set<ModView> enabledModViews = new HashSet<>();
        for (final ModDir modDir : enabledModDirs)
        {
            final InstalledMod installedActiveMod = installedMods.stream()
                    .filter(mod -> modDir.getDirName().equals(mod.getModDirectoryName()))
                    .findFirst()
                    .orElse(null);

            if (installedActiveMod == null)
                continue;
            enabledModViews.add(new ModView(installedActiveMod.getName(), modDir.isServerMod(), installedActiveMod.getPreviewUrl(), modWorkshopUrlBuilder.buildUrlForFileId(installedActiveMod.getPublishedFileId())));
        }

        modsView.setDisabledMods(disabledModViews);
        modsView.setEnabledMods(enabledModViews);
        return modsView;
    }
}
