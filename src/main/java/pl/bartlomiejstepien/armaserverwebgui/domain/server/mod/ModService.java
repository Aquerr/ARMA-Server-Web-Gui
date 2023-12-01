package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.springframework.http.codec.multipart.FilePart;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.EnabledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.InstalledFileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface ModService
{
    Mono<InstalledModEntity> saveModFile(FilePart multipartFile);

    Mono<ModsView> getModsView();

    Mono<Void> installModFromWorkshop(long fileId, String modName);

    List<WorkshopModInstallationRequest> getWorkShopModInstallRequests();

    Mono<InstalledModEntity> saveToDB(InstalledModEntity installedModEntity);

    Mono<Void> deleteFromDB(long id);

    List<InstalledFileSystemMod> getInstalledModsFromFileSystem();

    Mono<Boolean> deleteMod(String modName);

    void saveEnabledModList(Set<EnabledMod> workshopModIds);

    Flux<InstalledModEntity> getInstalledMods();

    Flux<ArmaWorkshopMod> getInstalledWorkshopMods();
}
