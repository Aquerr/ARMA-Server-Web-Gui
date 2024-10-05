package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModSettingsService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettings;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/mods/settings")
@AllArgsConstructor
public class ModSettingsRestController
{
    private final ModSettingsService modSettingsService;

    @GetMapping
    public Flux<ModSettings> getModSettings()
    {
        return modSettingsService.getModSettingsWithoutContents();
    }

    @GetMapping("/{id}/content")
    public Mono<String> getModSettingsContent(@PathVariable("id") long id)
    {
        return modSettingsService.getModSettingsContent(id);
    }

    @PutMapping("/{id}/content")
    public Mono<Void> saveModSettingsContent(@PathVariable("id") long id, @RequestBody ModSettingsContent modSettingsContent)
    {
        return modSettingsService.saveModSettingsContent(id, modSettingsContent.getContent());
    }

    @PutMapping("/{id}")
    public Mono<Void> updateModSettings(@PathVariable("id") long id, @RequestBody ModSettings modSettings)
    {
        return modSettingsService.saveModSettings(modSettings);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteModSettings(@PathVariable("id") long id)
    {
        return modSettingsService.deleteModSettings(id);
    }

    @Data
    public static final class ModSettingsContent
    {
        private String content;
    }
}
