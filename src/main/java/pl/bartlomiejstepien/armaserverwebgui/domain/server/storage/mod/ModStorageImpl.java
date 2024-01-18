package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import net.lingala.zip4j.ZipFile;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModFolderNameHelper;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.exception.CouldNotReadModMetaFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotInstallWorkshopModException;
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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Repository
public class ModStorageImpl implements ModStorage
{
    private final Supplier<Path> modDirectory;
    private final InstalledModRepository installedModRepository;

    private final ModFolderNameHelper modFolderNameHelper;

    public ModStorageImpl(ASWGConfig aswgConfig,
                          InstalledModRepository installedModRepository,
                          ModFolderNameHelper modFolderNameHelper)
    {
        this.modDirectory = () -> Paths.get(aswgConfig.getServerDirectoryPath());
        this.installedModRepository = installedModRepository;
        this.modFolderNameHelper = modFolderNameHelper;
    }

    @Override
    public Mono<Path> save(FilePart multipartFile) throws IOException
    {
        Path filePath = modDirectory.get().resolve(modFolderNameHelper.buildFor(multipartFile));
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
        return Files.exists(modDirectory.get().resolve(modFolderNameHelper.buildFor(filename)));
    }

    @Override
    public List<InstalledFileSystemMod> getInstalledModsFromFileSystem()
    {
        return Optional.ofNullable(modDirectory.get().toFile().listFiles())
                .map(files -> Stream.of(files)
                        .filter(this::isModDirectory)
                        .map(this::getInstalledFileSystemModFromDirectory)
                        .filter(Objects::nonNull)
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public ModMetaFile readModMetaFile(Path modDirectory) throws CouldNotReadModMetaFile
    {
        return ModMetaFile.forFile(modDirectory.resolve("meta.cpp"));
    }

    @Override
    public Mono<Boolean> deleteMod(InstalledModEntity installedModEntity)
    {
        final File[] files = this.modDirectory.get().toFile().listFiles();
        if (files != null)
        {
            for (final File file : files)
            {
                if (file.getName().equals(installedModEntity.getModDirectoryName()))
                {
                    FileSystemUtils.deleteRecursively(file);
                }
            }
        }

        return this.installedModRepository.delete(installedModEntity)
                .thenReturn(true)
                .onErrorReturn(false);
    }

    @Override
    public Mono<InstalledModEntity> getInstalledMod(String modName)
    {
        return this.installedModRepository.findByName(modName);
    }

    @Override
    public Path copyModFolderFromSteamCmd(Path steamCmdModFolderPath, Path armaServerDir, String modName)
    {
        String normalizedModDirectoryName = modFolderNameHelper.buildFor(modName);
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
            throw new CouldNotInstallWorkshopModException(e.getMessage(), e);
        }
        return modDirectoryPath;
    }

    @Override
    public Path linkModFolderToSteamCmdModFolder(Path steamCmdModFolderPath, Path armaServerDir, String modName)
    {
        String normalizedModDirectoryName = modFolderNameHelper.buildFor(modName);
        Path modDirectoryPath = armaServerDir.resolve(normalizedModDirectoryName);
        try
        {
            Files.createSymbolicLink(modDirectoryPath, steamCmdModFolderPath);
            normalizeEachFileNameInFolderRecursively(modDirectoryPath); // TO REMOVE ?
        }
        catch (IOException e)
        {
            throw new CouldNotInstallWorkshopModException(e.getMessage(), e);
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
                    Path newFilePath = file.resolveSibling(modFolderNameHelper.normalize(file.getFileName().toString()));
                    file.toFile().renameTo(newFilePath.toAbsolutePath().toFile());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                {
                    Path newFilePath = dir.resolveSibling(modFolderNameHelper.normalize(dir.getFileName().toString()));
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
        Path newModFolderPath = modFolder.toPath().resolveSibling(modFolderNameHelper.normalize(modFolder.getName()));
        modFolder.renameTo(newModFolderPath.toFile());
        return newModFolderPath;
    }

    private InstalledFileSystemMod getInstalledFileSystemModFromDirectory(File file)
    {
        if (Files.notExists(file.toPath()))
        {
            return null;
        }

        String directoryPath = file.getPath();
        return InstalledFileSystemMod.from(Paths.get(directoryPath));
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

    private boolean isModDirectory(File file)
    {
        return file.isDirectory() && file.getName().startsWith("@");
    }
}
