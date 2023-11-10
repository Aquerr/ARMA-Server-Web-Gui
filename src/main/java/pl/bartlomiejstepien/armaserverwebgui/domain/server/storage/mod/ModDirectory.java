package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import lombok.Value;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.exception.CouldNotReadModMetaFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Value(staticConstructor = "from")
public class ModDirectory
{
    Path path;

    public boolean hasModMetaFile()
    {
        return Files.exists(path.resolve("meta.cpp"));
    }

    public Path getKeysDirectory()
    {
        Path keysDirectory = path.resolve("keys");
        Path keyDirectory = path.resolve("key");
        return Files.exists(keysDirectory) ? keysDirectory : (Files.exists(keyDirectory) ? keyDirectory : null);
    }

    public ModMetaFile readModMetaFile() throws CouldNotReadModMetaFile
    {
        return ModMetaFile.forFile(path.resolve("meta.cpp"));
    }

    public String getName()
    {
        return this.path.getFileName().toString();
    }
}
