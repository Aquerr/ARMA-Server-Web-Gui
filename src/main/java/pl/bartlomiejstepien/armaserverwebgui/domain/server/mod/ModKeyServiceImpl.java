package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.InstalledFileSystemMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A service used to copy Mod Keys to Server keys directory.
 */
@Service
public class ModKeyServiceImpl implements ModKeyService
{
    private final Supplier<Path> serverKeysDirectory;

    private static final String KEYS_DIRECTORY_NAME = SystemUtils.isWindows() ? "Keys" : "keys";

    @Autowired
    public ModKeyServiceImpl(ASWGConfig aswgConfig)
    {
        this.serverKeysDirectory = () -> Paths.get(aswgConfig.getServerDirectoryPath() + File.separator + KEYS_DIRECTORY_NAME);
    }

    @Override
    public void copyKeysForMod(InstalledFileSystemMod installedFileSystemMod)
    {
        Path modKeysDirectoryPath = installedFileSystemMod.getModDirectory().getKeysDirectory();
        if (modKeysDirectoryPath == null)
            return; // Mod has no keys directory... skip...

        try
        {
            if (Files.notExists(serverKeysDirectory.get()))
                Files.createDirectories(serverKeysDirectory.get());

            doCopyKeysToServerKeysDirectory(getKeyFilesPaths(modKeysDirectoryPath));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteKeysForMod(InstalledFileSystemMod installedFileSystemMod)
    {
        Path modKeysDirectoryPath = installedFileSystemMod.getModDirectory().getKeysDirectory();
        if (modKeysDirectoryPath == null)
            return; // Mod has no keys directory... skip...

        try
        {
            List<String> keyFileNames = getKeyFileNamesForMod(modKeysDirectoryPath);
            Path serverKeysDirectoryPath = serverKeysDirectory.get();
            for (final String keyFileName : keyFileNames)
            {
                Files.deleteIfExists(serverKeysDirectoryPath.resolve(keyFileName));
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void clearServerKeys()
    {
        File serverKeysDir = this.serverKeysDirectory.get().toFile();
        File[] files = serverKeysDir.listFiles();
        if (files == null)
            return;

        for (final File file : files)
        {
            file.delete();
        }
    }

    private List<Path> getKeyFilesPaths(Path modKeysDir) throws IOException
    {
        return Files.list(modKeysDir).collect(Collectors.toList());
    }

    private List<String> getKeyFileNamesForMod(Path modKeysDir) throws IOException
    {
        return getKeyFilesPaths(modKeysDir).stream()
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    private void doCopyKeysToServerKeysDirectory(List<Path> keyPaths) throws IOException
    {
        Path serverKeysDirectoryPath = serverKeysDirectory.get();
        for (final Path keyPath : keyPaths)
        {
            Files.copy(keyPath, serverKeysDirectoryPath.resolve(keyPath.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}