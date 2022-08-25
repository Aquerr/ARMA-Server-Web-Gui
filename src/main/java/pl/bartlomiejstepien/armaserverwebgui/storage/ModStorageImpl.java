package pl.bartlomiejstepien.armaserverwebgui.storage;

import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class ModStorageImpl implements ModStorage
{
    private final Path modDirectory;

    public ModStorageImpl(ASWGConfig aswgConfig)
    {
        this.modDirectory = Paths.get(aswgConfig.getServerDirectoryPath());
    }

    @Override
    public List<String> getInstalledModNames()
    {
        return Optional.ofNullable(modDirectory.toFile().listFiles())
                .map(files -> Stream.of(files)
                        .map(File::getName)
                        .filter(name -> name.startsWith("@"))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
