package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.FileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModDirectory;

public interface ModKeyService
{

    void copyKeysForMod(ModDirectory modDirectory);

    void deleteKeysForMod(FileSystemMod fileSystemMod);

    void clearServerKeys();
}
