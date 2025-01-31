package pl.bartlomiejstepien.armaserverwebgui.web;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<GetModsResponse> getMods()
    {
        return this.modService.getModsView().map(GetModsResponse::of);
    }

    @HasPermissionModsUpdate
    @PostMapping("/enabled")
    public Mono<ResponseEntity<?>> saveEnabledModsList(@RequestBody SaveEnabledModsListRequest request)
    {
        return this.modService.saveEnabledModList(request.getMods())
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @HasPermissionModsUpload
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> uploadModFile(@RequestPart("file") Flux<FilePart> multipartFile)
    {
        return multipartFile
                .doOnNext(modFileValidator::validate)
                .doOnNext(filePart -> log.info("Uploading mod '{}' ", filePart.filename()))
                .flatMap(modService::saveModFile)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @HasPermissionModsDelete
    @DeleteMapping(value = "/{modName}")
    public Mono<ResponseEntity<Object>> deleteMod(@PathVariable("modName") String modName)
    {
        return this.modService.deleteMod(modName)
                .thenReturn(ResponseEntity.ok().build())
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    @HasPermissionModPresetsView
    @GetMapping("/presets-names")
    public Mono<ModPresetNamesResponse> getModPresetsNames()
    {
        return this.modPresetService.getModPresetsNames().collectList().map(ModPresetNamesResponse::of);
    }

    @HasPermissionModPresetsView
    @GetMapping("/presets/{id}")
    public Mono<ModPreset> getModPreset(@PathVariable("id") Long id)
    {
        return this.modPresetService.getModPreset(id);
    }

    @HasPermissionModPresetsAdd
    @PutMapping("/presets/{name}")
    public Mono<PresetSaveResponse> savePreset(@PathVariable("name") String name, @RequestBody PresetSaveRequest request)
    {
        return this.modPresetService.saveModPreset(ModPresetSaveParams.of(name, request.modNames))
                .then(Mono.just(new PresetSaveResponse(true)));
    }

    @HasPermissionModPresetsDelete
    @DeleteMapping("/presets/{name}")
    public Mono<PresetDeleteResponse> deletePreset(@PathVariable("name") String presetName)
    {
        return this.modPresetService.deletePreset(presetName)
                .thenReturn(new PresetDeleteResponse(true));
    }

    @HasPermissionModPresetsAdd
    @PostMapping("/presets/import")
    public Mono<Void> importPreset(@RequestBody PresetImportRequest request)
    {
        return this.modPresetService.importPreset(PresetImportParams.of(request.getName(), request.getModParams().stream()
                .map(param -> PresetImportParams.ModParam.of(param.getTitle(), param.getId())).toList()));
    }

    @HasPermissionModPresetsSelect
    @PostMapping("/presets/select")
    public Mono<Void> selectPreset(@RequestBody PresetSelectRequest request)
    {
        return this.modPresetService.selectPreset(request.getName());
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
