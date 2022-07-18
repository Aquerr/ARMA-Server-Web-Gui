package pl.bartlomiejstepien.armaserverwebgui.storage;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Repository
public class MissionStorageImpl implements MissionStorage
{
    private final ASWGConfig aswgConfig;
    private final Path missionsDirectory;

    @Autowired
    public MissionStorageImpl(final ASWGConfig aswgConfig)
    {
        this.aswgConfig = aswgConfig;
        this.missionsDirectory = Paths.get(aswgConfig.getServerDirectoryPath() + File.separator + "Missions");
    }

    @Override
    public Mono<Void> save(FilePart multipartFile) throws IOException
    {
        Files.createDirectories(missionsDirectory);
        return multipartFile.transferTo(missionsDirectory.resolve(multipartFile.filename()));
    }

    @Override
    public boolean doesMissionExists(String filename)
    {
        return Files.exists(missionsDirectory.resolve(filename));
    }
}
