package pl.bartlomiejstepien.armaserverwebgui.web;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModsDelete;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModsUpdate;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.EnabledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Mod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModsCollection;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;

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

    @HasPermissionModsDelete
    @DeleteMapping
    public ResponseEntity<?> deleteMod(@RequestBody DeleteModRequest request)
    {
        this.modService.deleteMod(request.getName());
        return ResponseEntity.ok().build();
    }

    @HasPermissionModsUpdate
    @PostMapping("/manage")
    public ResponseEntity<?> manageMod(@RequestBody ManageModsRequest request)
    {
        this.modService.manageMod(request.getName());
        return ResponseEntity.ok().build();
    }

    @HasPermissionModsDelete
    @DeleteMapping("/not-managed")
    public ResponseEntity<?> deleteNotManagedMod(@RequestBody DeleteNotManagedModRequest request)
    {
        this.modService.deleteNotManagedMod(request.getDirectoryName());
        return ResponseEntity.ok().build();
    }

    @Value(staticConstructor = "of")
    public static class GetModsResponse
    {
        List<Mod> disabledMods;
        List<Mod> enabledMods;
        List<Mod> notManagedMods;

        private static GetModsResponse of(ModsCollection modsCollection)
        {
            return new GetModsResponse(
                    modsCollection.getDisabledMods(),
                    modsCollection.getEnabledMods(),
                    modsCollection.getNotManagedMods()
            );
        }
    }

    @Data
    public static class SaveEnabledModsListRequest
    {
        Set<EnabledMod> mods = new HashSet<>();
    }

    @Data
    public static class DeleteModRequest
    {
        String name;
    }

    @Data
    public static class DeleteNotManagedModRequest
    {
        String directoryName;
    }

    @Data
    public static class ManageModsRequest
    {
        String name;
    }
}
