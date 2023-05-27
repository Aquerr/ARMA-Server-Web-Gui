package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.springframework.http.codec.multipart.FilePart;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModView;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface ModService
{
    Mono<InstalledMod> saveModFile(FilePart multipartFile);

    ModsView getModsView();

    Mono<InstalledMod> installModFromWorkshop(long fileId, String modName);

    List<WorkshopModInstallationRequest> getWorkShopModInstallRequests();

    Mono<InstalledMod> saveToDB(InstalledMod installedMod);

    Mono<Void> deleteFromDB(long id);

    List<InstalledMod> getInstalledModsFromFileSystem();

    Mono<Boolean> deleteMod(String modName);

    void saveEnabledModList(Set<ModView> modDirs);

    Flux<InstalledMod> getInstalledMods();

    Flux<ArmaWorkshopMod> getInstalledWorkshopMods();
}
