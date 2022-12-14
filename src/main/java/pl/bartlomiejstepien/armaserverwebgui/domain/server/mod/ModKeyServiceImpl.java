package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A service used to copy Mod Keys to Server keys directory.
 */
@Service
public class ModKeyServiceImpl implements ModKeyService
{
    private static final String MOD_KEYS_FOLDER_NAME = "keys";

    @Override
    public void copyKeysForMod(Path modFolder)
    {

    }

    @Override
    public void deleteKeysForMod(Path modFolder)
    {

    }

    private Path getKeyPathsForMod(Path modFolder)
    {
        Path modKeysFolder = modFolder.resolve("keys");
        if (Files.exists(modKeysFolder))
        {

        }
        modKeysFolder = modFolder.resolve("Keys");
        if (Files.exists(modKeysFolder))
        {

        }
        return null;
    }
}
