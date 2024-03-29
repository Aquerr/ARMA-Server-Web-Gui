package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
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
import java.util.stream.Stream;

@Repository
public class MissionStorageImpl implements MissionStorage
{
    private final MissionFileNameHelper missionFileNameHelper;
    private final Supplier<Path> missionsDirectory;

    @Autowired
    public MissionStorageImpl(final ASWGConfig aswgConfig, final MissionFileNameHelper missionFileNameHelper)
    {
        this.missionsDirectory = () -> Paths.get(aswgConfig.getServerDirectoryPath() + File.separator + "mpmissions");
        this.missionFileNameHelper = missionFileNameHelper;
    }

    @Override
    public Mono<Void> save(FilePart multipartFile) throws IOException
    {
        Files.createDirectories(missionsDirectory.get());
        return multipartFile.transferTo(missionsDirectory.get().resolve(multipartFile.filename().toLowerCase()));
    }

    @Override
    public boolean doesMissionExists(String filename)
    {
        return Files.exists(missionsDirectory.get().resolve(filename.toLowerCase()));
    }

    @Override
    public List<String> getInstalledMissionNames()
    {
        return Optional.ofNullable(missionsDirectory.get().toFile().listFiles())
                .map(files -> Stream.of(files)
                        .filter(this.missionFileNameHelper::isMissionFile)
                        .map(this.missionFileNameHelper::resolveMissionNameFromFile)
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public boolean deleteMission(String missionName)
    {
        final File[] files = this.missionsDirectory.get().toFile().listFiles();
        if (files != null)
        {
            for (final File file : files)
            {
                if (file.getName().equals(missionFileNameHelper.resolveFileName(missionName)))
                {
                    return file.delete();
                }
            }
        }

        return false;
    }
}
