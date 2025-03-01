package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.InstalledFileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModDirectory;

public interface ModKeyService
{

    void copyKeysForMod(ModDirectory modDirectory);

    void deleteKeysForMod(InstalledFileSystemMod installedFileSystemMod);

    void clearServerKeys();
}
