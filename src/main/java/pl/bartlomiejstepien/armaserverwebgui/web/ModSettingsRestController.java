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

    @GetMapping
    public Flux<ModSettingsHeader> getModSettings()
    {
        return modSettingsService.getModSettingsWithoutContents();
    }

    @GetMapping("/{id}")
    public Mono<ModSettingsHeader> getModSettings(@PathVariable("id") long id)
    {
        return modSettingsService.getModSettingsWithoutContents(id);
    }

    @GetMapping("/{id}/content")
    public Mono<ModSettingsContent> getModSettingsContent(@PathVariable("id") long id)
    {
        return modSettingsService.getModSettingsContent(id).map(ModSettingsContent::new);
    }

    @PutMapping("/{id}")
    public Mono<ModSettingsHeader> updateModSettings(@PathVariable("id") long id,
                                        @RequestBody ModSettings modSettings)
    {
        modSettings.setId(id);
        return modSettingsService.saveModSettings(modSettings);
    }

    @PostMapping
    public Mono<ModSettingsHeader> createNewModSettings(@RequestBody ModSettings modSettings)
    {
        return modSettingsService.saveModSettings(modSettings);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteModSettings(@PathVariable("id") long id)
    {
        return modSettingsService.deleteModSettings(id);
    }
}
