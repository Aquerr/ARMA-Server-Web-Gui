package pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.AswgJob;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.JobExecutionInfoService;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.AswgJobNames;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class DifficultyScanJob extends AswgJob
{
    private final ASWGConfig aswgConfig;
    private final DifficultyService difficultyService;

    @Autowired
    public DifficultyScanJob(JobExecutionInfoService jobExecutionInfoService,
                             ASWGConfig aswgConfig,
                             DifficultyService difficultyService)
    {
        super(jobExecutionInfoService);
        this.aswgConfig = aswgConfig;
        this.difficultyService = difficultyService;
    }

    @Override
    public String getName()
    {
        return AswgJobNames.DIFFICULTY_SCAN;
    }

    @Override
    @Transactional
    public void runJob()
    {
        if (!this.aswgConfig.getJobsProperties().isDifficultyProfileScannerEnabled())
        {
            log.info("Difficulty scanner is disabled. Skipping...");
        }

        scanDifficultyProfilesDirectoryForNewProfiles();
    }

    private void scanDifficultyProfilesDirectoryForNewProfiles()
    {
        Path difficultiesDirectoryPath = this.difficultyService.getProfilesDirectory();
        if (!Files.exists(difficultiesDirectoryPath))
            return;

        List<String> profileDirNames = Arrays.stream(difficultiesDirectoryPath.toFile().list())
                .filter(Objects::nonNull)
                .filter(profileName -> Files.exists(resolveDifficultyPath(profileName)))
                .toList();

        List<String> existingProfileNames = difficultyService.getDifficultyProfiles().stream()
                .map(DifficultyProfile::getName)
                .toList();
        List<String> newDifficultyProfiles = profileDirNames.stream()
                .filter(name -> !existingProfileNames.contains(name))
                .toList();

        for (String name : newDifficultyProfiles)
        {
            this.difficultyService.importDifficultyProfileFromFile(name);
        }
    }

    private Path resolveDifficultyPath(String difficultyName)
    {
        return this.difficultyService.getProfilesDirectory()
                .resolve(difficultyName)
                .resolve(difficultyName + DifficultyServiceImpl.PROFILE_SUFFIX);
    }
}
