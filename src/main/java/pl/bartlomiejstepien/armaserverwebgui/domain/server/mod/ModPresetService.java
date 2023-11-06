package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModPreset;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.PresetImportParams;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ModPresetService
{
    Flux<String> getModPresetsNames();

    Flux<ModPreset> getModPresets();

    Mono<Void> saveModPreset(ModPreset modPreset);

    Mono<ModPreset> getModPreset(Long id);

    Mono<ModPreset> getModPreset(String name);

    Mono<Void> importPreset(PresetImportParams params);

    Mono<Void> selectPreset(String name);

    Mono<Void> deletePreset(String presetName);
}
