package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.dotnet.DotnetDateTimeUtils;

import java.nio.file.Path;
import java.time.OffsetDateTime;

import static java.util.Optional.ofNullable;

@Getter
@EqualsAndHashCode
@ToString
public class InstalledFileSystemMod
{
    private final ModDirectory modDirectory;

    private InstalledFileSystemMod(ModDirectory modDirectory)
    {
        this.modDirectory = modDirectory;
    }

    public static InstalledFileSystemMod from(Path modDirectory)
    {
        return new InstalledFileSystemMod(ModDirectory.from(modDirectory));
    }

    public static InstalledFileSystemMod from(ModDirectory modDirectory)
    {
        return new InstalledFileSystemMod(modDirectory);
    }

    public long getWorkshopFileId()
    {
        return ofNullable(this.modDirectory.getMetaCppFile()).map(MetaCppFile::getPublishedFileId).orElse(0L);
    }

    public String getName()
    {
        return this.modDirectory.getModName();
    }

    public boolean isValid()
    {
        return ofNullable(this.modDirectory.getMetaCppFile()).isPresent();
    }

    public OffsetDateTime getLastUpdated()
    {
        return ofNullable(this.modDirectory.getMetaCppFile())
                .map(MetaCppFile::getTimestamp)
                .map(DotnetDateTimeUtils::dotnetTicksToOffsetDateTime)
                .orElse(null);
    }
}
