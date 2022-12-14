package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import java.nio.file.Path;

public interface ModKeyService
{

    void copyKeysForMod(Path modFolder);

    void deleteKeysForMod(Path modFolder);
}
