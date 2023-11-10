package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.converter.ModPresetConverter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModPreset;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.PresetImportParams;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.repository.ModPresetEntryRepository;
import pl.bartlomiejstepien.armaserverwebgui.repository.ModPresetRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ModPresetServiceImpl implements ModPresetService
{
    private final ModService modService;
    private final WorkShopModInstallService workShopModInstallService;
    private final ModPresetConverter modPresetConverter;
    private final ModPresetRepository modPresetRepository;
    private final ModPresetEntryRepository modPresetEntryRepository;

    @Override
    public Flux<String> getModPresetsNames()
    {
        return this.modPresetRepository.findAll()
                .map(ModPresetEntity::getName);
    }

    @Override
    public Flux<ModPreset> getModPresets()
    {
        return Flux.zip(
                modPresetRepository.findAll(),
                modPresetEntryRepository.findAll().collectList()
        ).map(this::mapToModPresets);
    }

    private ModPreset mapToModPresets(Tuple2<ModPresetEntity, List<ModPresetEntity.EntryEntity>> tuple)
    {
        List<ModPresetEntity.EntryEntity> entries = tuple.getT2().stream()
                .filter(entryEntity -> entryEntity.getModPresetId().equals(tuple.getT1().getId()))
                .toList();

        return this.modPresetConverter.convert(tuple.getT1(), entries);
    }

    @Override
    public Mono<Void> saveModPreset(ModPreset modPreset)
    {
        List<ModPresetEntity.EntryEntity> entries = this.modPresetConverter.convertToEntities(modPreset.getEntries());
        ModPresetEntity modPresetEntity = this.modPresetConverter.convert(modPreset);

        return modPresetRepository.save(modPresetEntity)
                .then(Mono.just(this.modPresetEntryRepository.saveAll(entries))).then();
    }

    @Override
    public Mono<ModPreset> getModPreset(Long id)
    {
        return Mono.zip(
                modPresetRepository.findById(id),
                modPresetEntryRepository.findAllByModPresetId(id).collectList()
        ).map(this::mapToModPresets);
    }

    @Override
    public Mono<ModPreset> getModPreset(String name)
    {
        return modPresetRepository.findByName(name)
                .flatMap(modPresetEntity -> Mono.zip(
                        Mono.just(modPresetEntity),
                        modPresetEntryRepository.findAllByModPresetId(modPresetEntity.getId()).collectList()
                ))
                .map(this::mapToModPresets);
    }

    @Override
    public Mono<Void> importPreset(PresetImportParams params)
    {
        // We should just trigger download of mods that are not yet downloaded.
        return this.modPresetRepository.findByName(params.getName())
                .defaultIfEmpty(new ModPresetEntity(null, params.getName()))
                .flatMap(this.modPresetRepository::save)
                .flatMap(modPresetEntity -> this.modPresetEntryRepository.findAllByModPresetId(modPresetEntity.getId())
                        .flatMap(this.modPresetEntryRepository::delete)
                        .then(Mono.just(this.modPresetConverter.convertToEntities(params.getModParams().stream()
                                        .map(modParam -> ModPreset.Entry.builder()
                                                .id(null)
                                                .name(modParam.getTitle())
                                                .modId(modParam.getId())
                                                .modPresetId(modPresetEntity.getId())
                                                .build()).toList()))))
                .log(log.getName())
                .map(this.modPresetEntryRepository::saveAll)
                .then(Mono.fromRunnable(() -> params.getModParams().forEach(modParam -> this.workShopModInstallService.queueWorkshopModInstallation(
                        new WorkshopModInstallationRequest(modParam.getId(), modParam.getTitle()))
                )));
    }

    @Override
    public Mono<Void> selectPreset(String name)
    {
        return getModPreset(name)
                .flatMap(modPreset -> Mono.empty().doFirst(() -> this.modService.saveEnabledModList(convertToModViews(modPreset.getEntries()))))
                .then();
    }

    @Override
    public Mono<Void> deletePreset(String presetName)
    {
        return this.modPresetRepository.findByName(presetName)
                .flatMap(modPresetEntity -> this.modPresetEntryRepository.findAllByModPresetId(modPresetEntity.getId())
                        .map(this.modPresetEntryRepository::delete)
                        .then(this.modPresetRepository.delete(modPresetEntity)));
    }

    private Set<ModView> convertToModViews(List<ModPreset.Entry> entries)
    {
        return entries.stream()
                .map(entry -> new ModView(entry.getName(), false, null, null))
                .collect(Collectors.toSet());
    }
}