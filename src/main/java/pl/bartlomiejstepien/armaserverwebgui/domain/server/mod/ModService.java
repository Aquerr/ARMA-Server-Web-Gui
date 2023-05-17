package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.springframework.http.codec.multipart.FilePart;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Mod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Mods;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface ModService
{
    Mono<Void> save(FilePart multipartFile);

    Mods getMods();

    List<String> getInstalledModNames();

    boolean deleteMod(String modName);

    void saveEnabledModList(Set<Mod> mods);

    List<ArmaWorkshopMod> getInstalledWorkshopMods();
}
