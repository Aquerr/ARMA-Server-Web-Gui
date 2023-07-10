package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledMod;

public interface ModKeyService
{

    void copyKeysForMod(InstalledMod installedMod);

    void deleteKeysForMod(InstalledMod installedMod);

    void clearServerKeys();
}
