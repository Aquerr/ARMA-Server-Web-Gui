package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.EnabledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter.ModPresetConverter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModPreset;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.PresetImportParams;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception.PresetNotFoundException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetSaveParams;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.repository.ModPresetEntryRepository;
import pl.bartlomiejstepien.armaserverwebgui.repository.ModPresetRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ModPresetServiceImpl implements ModPresetService
{
    private final ModService modService;
    private final SteamService steamService;
    private final ModPresetConverter modPresetConverter;
    private final ModPresetRepository modPresetRepository;
    private final ModPresetEntryRepository modPresetEntryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<String> getModPresetsNames()
    {
        return this.modPresetRepository.findAll().stream()
                .map(ModPresetEntity::getName)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModPreset> getModPresets()
    {
        List<ModPresetEntity.EntryEntity> modPresetEntities = modPresetEntryRepository.findAll();
        return modPresetRepository.findAll().stream()
                .map(entity -> mapToModPreset(entity, modPresetEntities))
                .toList();
    }

    private ModPreset mapToModPreset(ModPresetEntity entity, List<ModPresetEntity.EntryEntity> entryEntities)
    {
        List<ModPresetEntity.EntryEntity> entries = entryEntities.stream()
                .filter(entryEntity -> entryEntity.getModPresetId().equals(entity.getId()))
                .toList();

        return this.modPresetConverter.convert(entity, entries);
    }

    private void saveModPreset(ModPreset modPreset)
    {
        ModPresetEntity modPresetEntity = this.modPresetConverter.convert(modPreset);
        modPresetEntity = this.modPresetRepository.save(modPresetEntity);

        ModPresetEntity finalModPresetEntity = modPresetEntity;
        List<ModPresetEntity.EntryEntity> entryEntities = this.modPresetConverter.convertToEntities(modPreset.getEntries().stream()
                .map(entry -> entry.toBuilder().modPresetId(finalModPresetEntity.getId()).build())
                .toList());
        this.modPresetEntryRepository.saveAll(entryEntities);
    }

    @Override
    @Transactional
    public void saveModPreset(ModPresetSaveParams modPresetSaveParams)
    {
        ModPreset modPreset = Optional.ofNullable(getModPreset(modPresetSaveParams.getName()))
                .orElse(ModPreset.builder().name(modPresetSaveParams.getName()).build());

        clearModPresetEntries(modPreset);
        List<ModPreset.Entry> modPresetEntries = this.modService.getInstalledMods().stream()
                .filter(installedMod -> modPresetSaveParams.getModNames().contains(installedMod.getName()))
                .map(installedMod -> ModPreset.Entry.builder()
                        .modId(installedMod.getWorkshopFileId())
                        .name(installedMod.getName())
                        .modPresetId(modPreset.getId())
                        .build())
                .toList();

        ModPreset modPresetToSave = ModPreset.builder()
                        .id(modPreset.getId())
                        .name(modPreset.getName())
                        .entries(modPresetEntries)
                        .build();

        log.info("Saving mod preset: {}", modPresetToSave);
        saveModPreset(modPresetToSave);
    }

    private void clearModPresetEntries(ModPreset modPreset)
    {
        this.modPresetEntryRepository.deleteAll(this.modPresetEntryRepository.findAllByModPresetId(modPreset.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public ModPreset getModPreset(Long id)
    {
        ModPresetEntity modPresetEntity = modPresetRepository.findById(id).orElse(null);
        if (modPresetEntity == null)
            throw new PresetNotFoundException();

        return mapToModPreset(modPresetEntity, modPresetEntryRepository.findAllByModPresetId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ModPreset getModPreset(String name)
    {
        return modPresetRepository.findByName(name)
                .map(entity -> mapToModPreset(entity, modPresetEntryRepository.findAllByModPresetId(entity.getId())))
                .orElse(null);
    }

    @Override
    @Transactional
    public void importPreset(PresetImportParams params)
    {
        log.info("Importing preset: {}", params);
        ModPresetEntity modPresetEntity = this.modPresetRepository.findByName(params.getName()).orElse(null);
        if (modPresetEntity == null)
            modPresetEntity = new ModPresetEntity(null, params.getName());

        modPresetRepository.save(modPresetEntity);
        if (modPresetEntity.getId() != null)
        {
            modPresetEntryRepository.deleteAll(modPresetEntryRepository.findAllByModPresetId(modPresetEntity.getId()));
        }

        ModPresetEntity finalModPresetEntity = modPresetEntity;
        List<ModPresetEntity.EntryEntity> entryEntities = modPresetEntryRepository.saveAll(modPresetConverter.convertToEntities(params.getModParams().stream()
                .map(modParam -> ModPreset.Entry.builder()
                        .id(null)
                        .name(modParam.getTitle())
                        .modId(modParam.getId())
                        .modPresetId(finalModPresetEntity.getId())
                        .build())
                .toList()));

        entryEntities.stream()
                .map(entry -> new WorkshopModInstallationRequest(entry.getModId(), entry.getName()))
                .forEach(request -> steamService.scheduleWorkshopModDownload(request.getFileId(), request.getTitle(), false));
    }

    @Override
    @Transactional
    public void selectPreset(String name)
    {
        ModPreset modPreset = getModPreset(name);
        if (modPreset == null)
            throw new PresetNotFoundException();

        this.modService.saveEnabledModList(convertToModViews(modPreset.getEntries()));
    }

    @Override
    @Transactional
    public void deletePreset(String presetName)
    {
        ModPresetEntity modPresetEntity = this.modPresetRepository.findByName(presetName)
                .orElse(null);
        if (modPresetEntity == null)
            throw new PresetNotFoundException();

        log.info("Deleting mod preset: {}", presetName);
        this.modPresetEntryRepository.deleteAll(this.modPresetEntryRepository.findAllByModPresetId(modPresetEntity.getId()));
        this.modPresetRepository.delete(modPresetEntity);
    }

    private Set<EnabledMod> convertToModViews(List<ModPreset.Entry> entries)
    {
        return entries.stream()
                .map(entry -> new EnabledMod(entry.getModId(), false))
                .collect(Collectors.toSet());
    }
}
