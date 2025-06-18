package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.EnabledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.FileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;

import java.util.List;
import java.util.Set;

public interface ModService
{
    void saveModFile(MultipartFile multipartFile, boolean overwrite);

    boolean checkModFileExists(String modName);

    ModsView getModsView();

    void installModFromWorkshop(long fileId, String modName);

    List<WorkshopModInstallationRequest> getWorkShopModInstallRequests();

    InstalledModEntity saveToDB(InstalledModEntity installedModEntity);

    void deleteFromDB(long id);

    List<FileSystemMod> getInstalledModsFromFileSystem();

    void deleteMod(String modName);

    void saveEnabledModList(Set<EnabledMod> workshopModIds);

    List<InstalledModEntity> getInstalledMods();

    List<WorkshopMod> getInstalledWorkshopMods();

    void manageMod(String name);

    List<FileSystemMod> findNotManagedMods();

    void deleteNotManagedMod(String name);
}
