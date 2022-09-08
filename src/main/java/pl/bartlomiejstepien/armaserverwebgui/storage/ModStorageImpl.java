package pl.bartlomiejstepien.armaserverwebgui.storage;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

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
        return multipartFile.transferTo(modDirectory.get().resolve(multipartFile.filename()));
        ZipFile zipFile = new ZipFile(multipartFile);
    }

    @Override
    public boolean doesModExists(String filename)
    {
        return Files.exists(modDirectory.get().resolve(filename));
    }

    @Override
    public List<String> getInstalledModNames()
    {
        return Optional.ofNullable(modDirectory.get().toFile().listFiles())
                .map(files -> Stream.of(files)
                        .map(File::getName)
                        .filter(name -> name.startsWith("@"))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
