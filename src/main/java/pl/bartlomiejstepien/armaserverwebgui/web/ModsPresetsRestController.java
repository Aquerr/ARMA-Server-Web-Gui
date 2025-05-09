package pl.bartlomiejstepien.armaserverwebgui.web;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModPresetsAdd;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModPresetsDelete;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModPresetsSelect;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModPresetsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModPresetService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModPreset;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.PresetImportParams;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetSaveParams;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mods-presets")
@AllArgsConstructor
@Slf4j
public class ModsPresetsRestController
{
    private final ModPresetService modPresetService;

    @HasPermissionModPresetsView
    @GetMapping
    public ModPresetNamesResponse getModPresetsNames()
    {
        return ModPresetNamesResponse.of(this.modPresetService.getModPresetsNames());
    }

    @HasPermissionModPresetsView
    @GetMapping("/{id}")
    public ModPreset getModPreset(@PathVariable("id") Long id)
    {
        return this.modPresetService.getModPreset(id);
    }

    @HasPermissionModPresetsAdd
    @PutMapping("/{name}")
    public PresetSaveResponse savePreset(@PathVariable("name") String name, @RequestBody PresetSaveRequest request)
    {
        this.modPresetService.saveModPreset(ModPresetSaveParams.of(name, request.getModNames()));
        return new PresetSaveResponse(true);
    }

    @HasPermissionModPresetsDelete
    @DeleteMapping("/{name}")
    public PresetDeleteResponse deletePreset(@PathVariable("name") String presetName)
    {
        this.modPresetService.deletePreset(presetName);
        return new PresetDeleteResponse(true);
    }

    @HasPermissionModPresetsAdd
    @PostMapping("/import")
    public void importPreset(@RequestBody PresetImportRequest request)
    {
        this.modPresetService.importPreset(PresetImportParams.of(request.getName(), request.getModParams().stream()
                .map(param -> PresetImportParams.ModParam.of(param.getTitle(), param.getId())).toList()));
    }

    @HasPermissionModPresetsSelect
    @PostMapping("/select")
    public void selectPreset(@RequestBody PresetSelectRequest request)
    {
        this.modPresetService.selectPreset(request.getName());
    }

    @Value(staticConstructor = "of")
    public static class ModPresetNamesResponse
    {
        List<String> presets;
    }

    @Data
    public static class PresetImportRequest
    {
        private String name;
        private List<ModParam> modParams = new ArrayList<>();

        @Data
        static class ModParam
        {
            private Long id;
            private String title;
        }
    }

    @Data
    public static class PresetSelectRequest
    {
        private String name;
    }

    public record PresetDeleteResponse(boolean deleted)
    {
    }

    @Data
    public static class PresetSaveRequest
    {
        private String name;
        private List<String> modNames = new ArrayList<>();
    }

    @Value
    public static class PresetSaveResponse
    {
        boolean saved;
    }
}
