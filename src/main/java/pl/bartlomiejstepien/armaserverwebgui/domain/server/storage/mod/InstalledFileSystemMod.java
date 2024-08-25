package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;
import java.util.Optional;

@Getter
@EqualsAndHashCode
@ToString
public class InstalledFileSystemMod
{
    private Optional<ModMetaFile> modMetaFile = Optional.empty();

    private final ModDirectory modDirectory;

    private InstalledFileSystemMod(ModDirectory modDirectory)
    {
        this.modDirectory = modDirectory;
        try
        {
            ModMetaFile modMetaFile = modDirectory.readModMetaFile();
            this.modMetaFile = Optional.ofNullable(modMetaFile);
        }
        catch (Exception exception)
        {
            // Nothing
        }
    }

    public static InstalledFileSystemMod from(Path modDirectory)
    {
        return new InstalledFileSystemMod(ModDirectory.from(modDirectory));
    }

    public void refreshDataFromMetaFile()
    {
        try
        {
            ModMetaFile modMetaFile = modDirectory.readModMetaFile();

        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public long getWorkshopFileId()
    {
        return this.modMetaFile.map(ModMetaFile::getPublishedFileId).orElse(0L);
    }

    public String getName()
    {
        return this.modMetaFile.map(ModMetaFile::getName).orElse(modDirectory.getName());
    }

    public boolean isValid()
    {
        return modMetaFile.isPresent();
    }
}
