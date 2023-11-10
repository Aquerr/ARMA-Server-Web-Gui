package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.InstalledFileSystemMod;

public interface ModKeyService
{

    void copyKeysForMod(InstalledFileSystemMod installedFileSystemMod);

    void deleteKeysForMod(InstalledFileSystemMod installedFileSystemMod);

    void clearServerKeys();
}
