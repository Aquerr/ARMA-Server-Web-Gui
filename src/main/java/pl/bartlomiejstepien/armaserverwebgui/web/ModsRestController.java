package pl.bartlomiejstepien.armaserverwebgui.web;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModPresetsAdd;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModPresetsDelete;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModPresetsSelect;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModPresetsView;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModsDelete;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModsUpdate;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModsUpload;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.EnabledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModView;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModPresetService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModPreset;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.PresetImportParams;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetSaveParams;
import pl.bartlomiejstepien.armaserverwebgui.web.validator.ModFileValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/mods")
@AllArgsConstructor
@Slf4j
public class ModsRestController
{
    private final ModService modService;
    private final ModPresetService modPresetService;
    private final ModFileValidator modFileValidator;

    @HasPermissionModsView
    @GetMapping
    public GetModsResponse getMods()
    {
        return GetModsResponse.of(this.modService.getModsView());
    }

    @HasPermissionModsUpdate
    @PostMapping("/enabled")
    public ResponseEntity<?> saveEnabledModsList(@RequestBody SaveEnabledModsListRequest request)
    {
        this.modService.saveEnabledModList(request.getMods());
        return ResponseEntity.ok().build();
    }

    @HasPermissionModsUpload
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadModFile(@RequestPart("file") List<MultipartFile> multipartFiles)
    {
        for (MultipartFile multipartFile : multipartFiles)
        {
            modFileValidator.validate(multipartFile);
            log.info("Uploading mod '{}' ", multipartFile.getOriginalFilename());
            modService.saveModFile(multipartFile);
        }

        return ResponseEntity.ok().build();
    }

    @HasPermissionModsDelete
    @DeleteMapping(value = "/{modName}")
    public ResponseEntity<?> deleteMod(@PathVariable("modName") String modName)
    {
        this.modService.deleteMod(modName);
        return ResponseEntity.ok().build();
    }

    @HasPermissionModPresetsView
    @GetMapping("/presets-names")
    public ModPresetNamesResponse getModPresetsNames()
    {
        return ModPresetNamesResponse.of(this.modPresetService.getModPresetsNames());
    }

    @HasPermissionModPresetsView
    @GetMapping("/presets/{id}")
    public ModPreset getModPreset(@PathVariable("id") Long id)
    {
        return this.modPresetService.getModPreset(id);
    }

    @HasPermissionModPresetsAdd
    @PutMapping("/presets/{name}")
    public PresetSaveResponse savePreset(@PathVariable("name") String name, @RequestBody PresetSaveRequest request)
    {
        this.modPresetService.saveModPreset(ModPresetSaveParams.of(name, request.getModNames()));
        return new PresetSaveResponse(true);
    }

    @HasPermissionModPresetsDelete
    @DeleteMapping("/presets/{name}")
    public PresetDeleteResponse deletePreset(@PathVariable("name") String presetName)
    {
        this.modPresetService.deletePreset(presetName);
        return new PresetDeleteResponse(true);
    }

    @HasPermissionModPresetsAdd
    @PostMapping("/presets/import")
    public void importPreset(@RequestBody PresetImportRequest request)
    {
        this.modPresetService.importPreset(PresetImportParams.of(request.getName(), request.getModParams().stream()
                .map(param -> PresetImportParams.ModParam.of(param.getTitle(), param.getId())).toList()));
    }

    @HasPermissionModPresetsSelect
    @PostMapping("/presets/select")
    public void selectPreset(@RequestBody PresetSelectRequest request)
    {
        this.modPresetService.selectPreset(request.getName());
    }

    @Value(staticConstructor = "of")
    private static class GetModsResponse
    {
        Set<ModView> disabledMods;
        Set<ModView> enabledMods;

        private static GetModsResponse of(ModsView modsView)
        {
            return new GetModsResponse(modsView.getDisabledMods(), modsView.getEnabledMods());
        }
    }

    @Data
    private static class SaveEnabledModsListRequest
    {
        Set<EnabledMod> mods = new HashSet<>();
    }

    @Value(staticConstructor = "of")
    private static class ModPresetNamesResponse
    {
        List<String> presets;
    }

    @Data
    private static class PresetImportRequest
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
    private static class PresetSelectRequest
    {
        private String name;
    }

    @Value
    private static class PresetDeleteResponse
    {
        boolean deleted;
    }

    @Data
    private static class PresetSaveRequest
    {
        private String name;
        private List<String> modNames = new ArrayList<>();
    }

    @Value
    private static class PresetSaveResponse
    {
        boolean saved;
    }
}
