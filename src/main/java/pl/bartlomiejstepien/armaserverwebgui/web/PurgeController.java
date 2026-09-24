package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionsPurge;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModPresetsPurge;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModsPurge;
import pl.bartlomiejstepien.armaserverwebgui.domain.purge.DataPurgeService;

@RestController
@RequestMapping("/api/v1/settings/purge")
@RequiredArgsConstructor
public class PurgeController
{
    private final DataPurgeService dataPurgeService;

    @DeleteMapping("/missions")
    @HasPermissionMissionsPurge
    public ResponseEntity<?> purgeMissions(
            @RequestParam(value = "delete-files", required = false, defaultValue = "false") boolean deleteFiles)
    {
        this.dataPurgeService.purgeMissions(deleteFiles);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/mods")
    @HasPermissionModsPurge
    public ResponseEntity<?> purgeMods(
            @RequestParam(value = "delete-files", required = false, defaultValue = "false") boolean deleteFiles)
    {
        this.dataPurgeService.purgeMods(deleteFiles);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/mod-presets")
    @HasPermissionModPresetsPurge
    public ResponseEntity<?> purgeModPresets()
    {
        this.dataPurgeService.purgeModPresets();
        return ResponseEntity.ok().build();
    }
}
