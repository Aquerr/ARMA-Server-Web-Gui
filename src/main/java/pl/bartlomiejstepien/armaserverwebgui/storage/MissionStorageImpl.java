package pl.bartlomiejstepien.armaserverwebgui.storage;

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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public List<String> getInstalledMissionNames()
    {
        return Optional.ofNullable(missionsDirectory.toFile().listFiles())
                .map(files -> Stream.of(files)
                        .map(File::getName).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public boolean deleteMission(String missionName)
    {
        final File[] files = this.missionsDirectory.toFile().listFiles();
        if (files != null)
        {
            for (final File file : files)
            {
                if (file.getName().equals(missionName))
                {
                    return file.delete();
                }
            }
        }

        return false;
    }
}
