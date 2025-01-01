package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModSettingsAdd;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModSettingsDelete;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModSettingsUpdate;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModSettingsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModSettingsService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettingsContent;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettingsHeader;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettings;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/mods/settings")
@RequiredArgsConstructor
public class ModSettingsRestController
{
    private final ModSettingsService modSettingsService;

    @HasPermissionModSettingsView
    @GetMapping
    public Flux<ModSettingsHeader> getModSettings()
    {
        return modSettingsService.getModSettingsWithoutContents();
    }

    @HasPermissionModSettingsView
    @GetMapping("/{id}")
    public Mono<ModSettingsHeader> getModSettings(@PathVariable("id") long id)
    {
        return modSettingsService.getModSettingsWithoutContents(id);
    }

    @HasPermissionModSettingsView
    @GetMapping("/{id}/content")
    public Mono<ModSettingsContent> getModSettingsContent(@PathVariable("id") long id)
    {
        return modSettingsService.getModSettingsContent(id).map(ModSettingsContent::new);
    }

    @HasPermissionModSettingsUpdate
    @PutMapping("/{id}")
    public Mono<ModSettingsHeader> updateModSettings(@PathVariable("id") long id,
                                        @RequestBody ModSettings modSettings)
    {
        modSettings.setId(id);
        return modSettingsService.saveModSettings(modSettings);
    }

    @HasPermissionModSettingsAdd
    @PostMapping
    public Mono<ModSettingsHeader> createNewModSettings(@RequestBody ModSettings modSettings)
    {
        return modSettingsService.saveModSettings(modSettings);
    }

    @HasPermissionModSettingsDelete
    @DeleteMapping("/{id}")
    public Mono<Void> deleteModSettings(@PathVariable("id") long id)
    {
        return modSettingsService.deleteModSettings(id);
    }
}
