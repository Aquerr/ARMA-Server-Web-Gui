package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.dotnet.DotnetDateTimeUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;

import static java.util.Optional.ofNullable;

@Getter
@EqualsAndHashCode
@ToString
public class FileSystemMod
{
    private final ModDirectory modDirectory;

    private FileSystemMod(ModDirectory modDirectory)
    {
        this.modDirectory = modDirectory;
    }

    public static FileSystemMod from(Path modDirectory)
    {
        return new FileSystemMod(ModDirectory.from(modDirectory));
    }

    public static FileSystemMod from(ModDirectory modDirectory)
    {
        return new FileSystemMod(modDirectory);
    }

    public long getWorkshopFileId()
    {
        return ofNullable(this.modDirectory.getMetaCppFile()).map(MetaCppFile::getPublishedFileId).orElse(0L);
    }

    public String getName()
    {
        return this.modDirectory.getModName();
    }

    public boolean hasFiles()
    {
        return ofNullable(this.modDirectory.getMetaCppFile()).isPresent()
                && (Files.exists(this.modDirectory.getPath().resolve("Addons")) || Files.exists(this.modDirectory.getPath().resolve("addons")));
    }

    public OffsetDateTime getLastUpdated()
    {
        return ofNullable(this.modDirectory.getMetaCppFile())
                .map(MetaCppFile::getTimestamp)
                .map(DotnetDateTimeUtils::dotnetTicksToOffsetDateTime)
                .orElse(null);
    }
}
