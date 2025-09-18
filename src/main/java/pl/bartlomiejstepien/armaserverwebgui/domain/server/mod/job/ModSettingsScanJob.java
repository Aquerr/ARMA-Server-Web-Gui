package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.AswgJob;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.JobExecutionInfoService;
import pl.bartlomiejstepien.armaserverwebgui.application.util.AswgFileNameNormalizer;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.AswgJobNames;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModSettingsStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModSettingsEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ModSettingsScanJob extends AswgJob
{
    private static final String DEFAULT_MOD_SETTINGS_NAME = "default";

    private final ASWGConfig aswgConfig;
    private final ModSettingsStorage modSettingsStorage;
    private final AswgFileNameNormalizer fileNameNormalizer;

    @Autowired
    public ModSettingsScanJob(JobExecutionInfoService jobExecutionInfoService,
                              ASWGConfig aswgConfig,
                              ModSettingsStorage modSettingsStorage,
                              AswgFileNameNormalizer fileNameNormalizer)
    {
        super(jobExecutionInfoService);
        this.aswgConfig = aswgConfig;
        this.modSettingsStorage = modSettingsStorage;
        this.fileNameNormalizer = fileNameNormalizer;
    }

    @Override
    public String getName()
    {
        return AswgJobNames.MOD_SETTINGS_SCAN;
    }

    @Override
    @Transactional
    public void runJob()
    {
        log.info("Scanning for new mod/addon settings files...");
        if (!this.aswgConfig.getJobsProperties().isModSettingsScannerEnabled())
        {
            log.info("Mod/Addon settings scanner is disabled. Skipping...");
        }

        scanModSettingsForNewSettingsFiles();
    }

    private void scanModSettingsForNewSettingsFiles()
    {
        Path modSettingsDirPath = this.modSettingsStorage.getModSettingsDirPath();
        if (Files.notExists(modSettingsDirPath))
            return;

        List<ModSettingsEntity> existingModSettings = this.modSettingsStorage.findAll().stream()
                .toList();
        List<String> existingSettingsNames = existingModSettings.stream()
                .map(ModSettingsEntity::getName)
                .toList();


        List<String> settingsFileNames = Arrays.stream(modSettingsDirPath.toFile().list()).toList();

        Set<String> settingsToInstall = settingsFileNames.stream()
                .map(this::stripExtension)
                .map(this.fileNameNormalizer::normalize)
                .filter(name -> !existingSettingsNames.contains(name))
                .collect(Collectors.toSet());

        if (settingsToInstall.contains(ModSettingsStorage.ACTIVE_MOD_SETTINGS_NAME)
                && existingModSettings.stream().anyMatch(ModSettingsEntity::isActive))
        {
            settingsToInstall.remove(ModSettingsStorage.ACTIVE_MOD_SETTINGS_NAME);
        }

        for (String settingsName : settingsToInstall)
        {
            ModSettingsEntity modSettingsEntity;

            if (settingsName.equals(ModSettingsStorage.ACTIVE_MOD_SETTINGS_NAME))
            {
                modSettingsEntity = ModSettingsEntity.builder()
                        .name(DEFAULT_MOD_SETTINGS_NAME)
                        .active(true)
                        .build();
            }
            else
            {
                modSettingsEntity = ModSettingsEntity.builder()
                        .name(settingsName)
                        .active(false)
                        .build();
            }

            this.modSettingsStorage.save(modSettingsEntity);
        }
    }

    private String stripExtension(String fileName)
    {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
}
