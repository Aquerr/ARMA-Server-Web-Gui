package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.mod;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.FileUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class ModStorageImpl implements ModStorage
{
    private final Supplier<Path> modDirectory;

    public ModStorageImpl(ASWGConfig aswgConfig)
    {
        this.modDirectory = () -> Paths.get(aswgConfig.getServerDirectoryPath());
    }

    @Override
    public Mono<Void> save(FilePart multipartFile) throws IOException
    {
        Files.createDirectories(modDirectory.get());

        // Zapis .zip
        Path filePath = modDirectory.get().resolve(multipartFile.filename().replaceAll(" ", "_"));
        return saveFileAtPath(multipartFile, filePath).doOnSuccess(next -> {
            // Wypakowanie .zip
            unpackZipFile(filePath);
        }).doOnSuccess(next -> {
            try
            {
                // Usunięcie .zip
                deleteZipFile(filePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    private Mono<Void> saveFileAtPath(FilePart multipartFile, Path saveLocation)
    {
        return multipartFile.transferTo(saveLocation);
    }

    private void unpackZipFile(Path filePath)
    {
        try
        {
            ZipFile zipFile = new ZipFile(filePath.toAbsolutePath().toString());
            zipFile.extractAll(filePath.getParent().toAbsolutePath().toString());
            String modFolderName = zipFile.getFileHeaders().get(0).getFileName();
            Path newModFolderPath = renameModFolderToLowerCaseWithUnderscores(filePath.getParent().resolve(modFolderName));
            convertEachFileToLowercase(newModFolderPath);
        }
        catch (ZipException e)
        {
            e.printStackTrace();
        }
    }

    private void deleteZipFile(Path filePath) throws IOException
    {
        Files.deleteIfExists(filePath);
    }

    @Override
    public boolean doesModExists(String filename)
    {
        return Files.exists(modDirectory.get().resolve(filename.toLowerCase().replaceAll(" ", "_")));
    }

    @Override
    public List<String> getInstalledModNames()
    {
        return Optional.ofNullable(modDirectory.get().toFile().listFiles())
                .map(files -> Stream.of(files)
                        .filter(File::isDirectory)
                        .map(File::getName)
                        .filter(name -> name.startsWith("@"))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public boolean deleteMod(String modName)
    {
        final File[] files = this.modDirectory.get().toFile().listFiles();
        if (files != null)
        {
            for (final File file : files)
            {
                if (file.getName().equals(modName))
                {
                    FileSystemUtils.deleteRecursively(file);
                }
            }
        }

        return false;
    }

    private void convertEachFileToLowercase(Path filePath)
    {
        try
        {
            Files.walkFileTree(filePath, new FileVisitor<Path>()
            {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
                {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    Path newFilePath = file.resolveSibling(file.getFileName().toString().toLowerCase().replaceAll(" ", "_"));
                    file.toFile().renameTo(newFilePath.toAbsolutePath().toFile());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
                {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                {
                    Path newFilePath = dir.resolveSibling(dir.getFileName().toString().toLowerCase().replaceAll(" ", "_"));
                    dir.toFile().renameTo(newFilePath.toAbsolutePath().toFile());
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Path renameModFolderToLowerCaseWithUnderscores(Path modFolderPath)
    {
        File modFolder = modFolderPath.getParent().resolve(modFolderPath.getFileName()).toFile();
        Path newModFolderPath = modFolder.toPath().resolveSibling(modFolder.getName().toLowerCase().replaceAll(" ", "_"));
        modFolder.renameTo(newModFolderPath.toFile());
        return newModFolderPath;
    }
}
