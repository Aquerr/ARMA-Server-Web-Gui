package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModPreset;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.PresetImportParams;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetSaveParams;

import java.util.List;

public interface ModPresetService
{
    List<String> getModPresetsNames();

    List<ModPreset> getModPresets();

    void saveModPreset(ModPresetSaveParams modPresetSaveParams);

    ModPreset getModPreset(Long id);

    ModPreset getModPreset(String name);

    void importPreset(PresetImportParams params);

    void selectPreset(String name);

    void deletePreset(String presetName);
}
