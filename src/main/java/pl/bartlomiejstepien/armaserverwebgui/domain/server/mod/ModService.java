package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.springframework.http.codec.multipart.FilePart;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.InstalledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModView;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface ModService
{
    Mono<InstalledMod> save(FilePart multipartFile);

    ModsView getMods();

    List<InstalledMod> getInstalledMods();

    Mono<Boolean> deleteMod(String modName);

    void saveEnabledModList(Set<ModView> modDirs);

    Flux<ArmaWorkshopMod> getInstalledWorkshopMods();
}
