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
    private Optional<MetaCppFile> modMetaFile = Optional.empty();
    private Optional<ModCppFile> modFile = Optional.empty();

    private final ModDirectory modDirectory;

    private InstalledFileSystemMod(ModDirectory modDirectory)
    {
        this.modDirectory = modDirectory;
        try
        {
            MetaCppFile metaCppFile = modDirectory.readModMetaFile();
            this.modMetaFile = Optional.ofNullable(metaCppFile);
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
            MetaCppFile metaCppFile = modDirectory.readModMetaFile();

        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public long getWorkshopFileId()
    {
        return this.modMetaFile.map(MetaCppFile::getPublishedFileId).orElse(0L);
    }

    public String getName()
    {
        return this.modMetaFile.map(MetaCppFile::getName).orElse(modDirectory.getDirectoryName());
    }

    public boolean isValid()
    {
        return modMetaFile.isPresent();
    }
}
