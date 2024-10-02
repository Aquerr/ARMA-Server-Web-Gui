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
public class MissionFileFileStorageImpl implements MissionFileStorage
{
    private final MissionFileNameHelper missionFileNameHelper;
    private final Supplier<Path> missionsDirectory;

    @Autowired
    public MissionFileFileStorageImpl(final ASWGConfig aswgConfig, final MissionFileNameHelper missionFileNameHelper)
    {
        this.missionsDirectory = () -> Paths.get(aswgConfig.getServerDirectoryPath() + File.separator + "mpmissions");
        this.missionFileNameHelper = missionFileNameHelper;
    }

    @Override
    public Mono<Void> save(FilePart multipartFile) throws IOException
    {
        Files.createDirectories(missionsDirectory.get());
        String normalizedFileName = missionFileNameHelper.normalizeFileName(multipartFile.filename());
        return multipartFile.transferTo(missionsDirectory.get().resolve(normalizedFileName));
    }

    @Override
    public boolean doesMissionExists(String filename)
    {
        String normalizedFileName = missionFileNameHelper.normalizeFileName(filename);
        return Files.exists(missionsDirectory.get().resolve(normalizedFileName));
    }

    @Override
    public List<String> getInstalledMissionTemplates()
    {
        return Optional.ofNullable(missionsDirectory.get().toFile().listFiles())
                .map(files -> Stream.of(files)
                        .filter(this.missionFileNameHelper::isMissionFile)
                        .map(this.missionFileNameHelper::resolveMissionNameFromFile)
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public boolean deleteMission(String template)
    {
        final File[] files = this.missionsDirectory.get().toFile().listFiles();
        if (files != null)
        {
            for (final File file : files)
            {
                if (file.getName().equals(missionFileNameHelper.resolveFileName(template)))
                {
                    return file.delete();
                }
            }
        }

        return false;
    }
}
