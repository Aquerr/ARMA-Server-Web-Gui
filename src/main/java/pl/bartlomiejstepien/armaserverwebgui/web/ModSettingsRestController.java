package pl.bartlomiejstepien.armaserverwebgui.web;

import java.util.List;
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
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettings;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettingsContent;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.dto.ModSettingsHeader;

@RestController
@RequestMapping("/api/v1/mods/settings")
@RequiredArgsConstructor
public class ModSettingsRestController
{
    private final ModSettingsService modSettingsService;

    @HasPermissionModSettingsView
    @GetMapping
    public List<ModSettingsHeader> getModSettings()
    {
        return modSettingsService.getModSettingsWithoutContents();
    }

    @HasPermissionModSettingsView
    @GetMapping("/{id}")
    public ModSettingsHeader getModSettings(@PathVariable("id") long id)
    {
        return modSettingsService.getModSettingsWithoutContents(id);
    }

    @HasPermissionModSettingsView
    @GetMapping("/{id}/content")
    public ModSettingsContent getModSettingsContent(@PathVariable("id") long id)
    {
        return new ModSettingsContent(modSettingsService.getModSettingsContent(id));
    }

    @HasPermissionModSettingsUpdate
    @PutMapping("/{id}")
    public ModSettingsHeader updateModSettings(@PathVariable("id") long id,
                                               @RequestBody ModSettings modSettings)
    {
        modSettings.setId(id);
        return modSettingsService.saveModSettings(modSettings);
    }

    @HasPermissionModSettingsAdd
    @PostMapping
    public ModSettingsHeader createNewModSettings(@RequestBody ModSettings modSettings)
    {
        return modSettingsService.saveModSettings(modSettings);
    }

    @HasPermissionModSettingsDelete
    @DeleteMapping("/{id}")
    public void deleteModSettings(@PathVariable("id") long id)
    {
        modSettingsService.deleteModSettings(id);
    }
}
