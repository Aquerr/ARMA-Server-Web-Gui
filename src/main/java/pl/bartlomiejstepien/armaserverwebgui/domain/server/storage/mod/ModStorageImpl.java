package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import net.lingala.zip4j.ZipFile;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModFolderNameFactory;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledMod;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Repository
public class ModStorageImpl implements ModStorage
{
    private final Supplier<Path> modDirectory;
    private final InstalledModRepository installedModRepository;

    private final ModFolderNameFactory modFolderNameFactory;

    public ModStorageImpl(ASWGConfig aswgConfig,
                          InstalledModRepository installedModRepository,
                          ModFolderNameFactory modFolderNameFactory)
    {
        this.modDirectory = () -> Paths.get(aswgConfig.getServerDirectoryPath());
        this.installedModRepository = installedModRepository;
        this.modFolderNameFactory = modFolderNameFactory;
    }

    @Override
    public Mono<Path> save(FilePart multipartFile) throws IOException
    {
        Path filePath = modDirectory.get().resolve(modFolderNameFactory.buildFor(multipartFile));
        Mono<Void> blockingWrapper = Mono.fromRunnable(() -> {
            try
            {
                Files.createDirectories(modDirectory.get());
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        });
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic())
                .then(saveFileAtPath(multipartFile, filePath))
                .then(Mono.fromCallable(() -> unpackZipFile(filePath)))
                .doOnSuccess(next ->
                {
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

    @Override
    public boolean doesModExists(FilePart filename)
    {
        return Files.exists(modDirectory.get().resolve(modFolderNameFactory.buildFor(filename)));
    }

    @Override
    public List<InstalledMod> getInstalledModsFromFileSystem()
    {
        return Optional.ofNullable(modDirectory.get().toFile().listFiles())
                .map(files -> Stream.of(files)
                        .filter(File::isDirectory)
                        .filter(file -> file.getName().startsWith("@"))
                        .map(this::getInstalledModFromModDirectory)
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public ModMetaFile readModMetaFile(Path modDirectory)
    {
        return ModMetaFile.forFile(modDirectory.resolve("meta.cpp"));
    }

    @Override
    public Mono<Boolean> deleteMod(InstalledMod installedMod)
    {
        final File[] files = this.modDirectory.get().toFile().listFiles();
        if (files != null)
        {
            for (final File file : files)
            {
                if (file.getName().equals(installedMod.getModDirectoryName()))
                {
                    FileSystemUtils.deleteRecursively(file);
                }
            }
        }

        return this.installedModRepository.delete(installedMod)
                .thenReturn(true)
                .onErrorReturn(false);
    }

    @Override
    public Mono<InstalledMod> getInstalledMod(String modName)
    {
        return this.installedModRepository.findByName(modName);
    }

    @Override
    public Path copyModFolderFromSteamCmd(Path steamCmdModFolderPath, Path armaServerDir, String modName)
    {
        String normalizedModDirectoryName = "@" + modFolderNameFactory.normalize(modName);
        Path modDirectoryPath = armaServerDir.resolve(normalizedModDirectoryName);
        try
        {
            Files.createDirectories(modDirectoryPath);
            FileSystemUtils.copyRecursively(steamCmdModFolderPath, modDirectoryPath);
            normalizeEachFileNameInFolderRecursively(modDirectoryPath);
            FileSystemUtils.deleteRecursively(steamCmdModFolderPath);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return modDirectoryPath;
    }

    private void normalizeEachFileNameInFolderRecursively(Path filePath)
    {
        try
        {
            Files.walkFileTree(filePath, new SimpleFileVisitor<>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    Path newFilePath = file.resolveSibling(modFolderNameFactory.normalize(file.getFileName().toString()));
                    file.toFile().renameTo(newFilePath.toAbsolutePath().toFile());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                {
                    Path newFilePath = dir.resolveSibling(modFolderNameFactory.normalize(dir.getFileName().toString()));
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
        Path newModFolderPath = modFolder.toPath().resolveSibling(modFolderNameFactory.normalize(modFolder.getName()));
        modFolder.renameTo(newModFolderPath.toFile());
        return newModFolderPath;
    }

    private InstalledMod getInstalledModFromModDirectory(File file)
    {
        String directoryPath = file.getPath();
        ModMetaFile modMetaFile = readModMetaFile(file.toPath());

        return InstalledMod.builder()
                .directoryPath(directoryPath)
                .publishedFileId(modMetaFile.getPublishedFileId())
                .name(modMetaFile.getName())
                .build();
    }

    private Mono<Void> saveFileAtPath(FilePart multipartFile, Path saveLocation)
    {
        return multipartFile.transferTo(saveLocation);
    }

    private Path unpackZipFile(Path filePath)
    {
        try(ZipFile zipFile = new ZipFile(filePath.toAbsolutePath().toString()))
        {
            zipFile.extractAll(filePath.getParent().toAbsolutePath().toString());
            String modFolderName = zipFile.getFileHeaders().get(0).getFileName();
            Path newModFolderPath = renameModFolderToLowerCaseWithUnderscores(filePath.getParent().resolve(modFolderName));
            normalizeEachFileNameInFolderRecursively(newModFolderPath);
            return newModFolderPath;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private void deleteZipFile(Path filePath) throws IOException
    {
        Files.deleteIfExists(filePath);
    }
}
