package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import org.springframework.data.util.Lazy;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModDirectory
{
    private final Path path;
    private final Lazy<MetaCppFile> metaCppFile;
    private final Lazy<ModCppFile> modCppFile;

    public static ModDirectory from(Path modDirectory)
    {
        return new ModDirectory(modDirectory);
    }

    private ModDirectory(Path modDirectory)
    {
        this.path = modDirectory;
        this.metaCppFile = Lazy.of(() -> CppFileHelper.readFile(path.resolve(CppFileHelper.META_CPP), MetaCppFile.class));
        this.modCppFile = Lazy.of(() -> CppFileHelper.readFile(path.resolve(CppFileHelper.MOD_CPP), ModCppFile.class));
    }

    public boolean hasModMetaFile()
    {
        return Files.exists(path.resolve(CppFileHelper.META_CPP));
    }

    public Path getKeysDirectory()
    {
        Path keysDirectory = path.resolve("keys");
        Path keyDirectory = path.resolve("key");
        return Files.exists(keysDirectory) ? keysDirectory : (Files.exists(keyDirectory) ? keyDirectory : null);
    }

    public MetaCppFile readModMetaFile()
    {
        return metaCppFile.get();
    }

    public ModCppFile readModFile()
    {
        return modCppFile.get();
    }

    public Path getPath()
    {
        return path;
    }

    public MetaCppFile getMetaCppFile()
    {
        return metaCppFile.getNullable();
    }

    public ModCppFile getModCppFile()
    {
        return modCppFile.getNullable();
    }

    public String getDirectoryName()
    {
        return this.path.getFileName().toString();
    }

    public String getModName()
    {
        return metaCppFile.getOptional()
                .map(MetaCppFile::getName)
                .filter(StringUtils::hasText)
                .orElse(this.modCppFile.getOptional()
                        .map(ModCppFile::getName)
                        .filter(StringUtils::hasText)
                        .orElse(getDirectoryName()));
    }

    public long getSizeBytes()
    {
        try
        {
            return Files.walk(this.path)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .mapToLong(File::length)
                    .sum();
        }
        catch (IOException e)
        {
            return 0;
        }
    }
}
