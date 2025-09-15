package pl.bartlomiejstepien.armaserverwebgui.domain.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.AswgJob;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.AswgTaskScheduler;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.JobExecutionInfoService;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.model.JobSettings;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.DifficultyScanJob;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.job.MissionScannerJob;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job.InstallDeleteModsFromFilesystemJob;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job.ModSettingsScanJob;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job.ModUpdateJob;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Service
@AllArgsConstructor
public class JobService
{
    private final ASWGConfig aswgConfig;
    private final AswgTaskScheduler aswgTaskScheduler;
    private final JobExecutionInfoService jobExecutionInfoService;
    private final ModUpdateJob modUpdateJob;
    private final InstallDeleteModsFromFilesystemJob installDeleteModsFromFilesystemJob;
    private final DifficultyScanJob difficultyScanJob;
    private final ModSettingsScanJob modSettingsScanJob;
    private final MissionScannerJob missionScannerJob;

    private final Map<String, Supplier<JobSettings>> jobSettingsSuppliers = Map.of(
            AswgJobNames.MOD_UPDATE, this::getModUpdateJobSettings,
            AswgJobNames.INSTALL_DELETE_MODS, this::getInstallDeleteModsJobSettings,
            AswgJobNames.DIFFICULTY_SCAN, this::getDifficultyScanJobSettings,
            AswgJobNames.MOD_SETTINGS_SCAN, this::getModSettingsScanJobSettings,
            AswgJobNames.MISSIONS_SCANNER, this::getMissionScanJobSettings
    );

    private final Map<String, Consumer<JobSettingsUpdateParams>> jobSettingsUpdateConsumers = Map.of(
            AswgJobNames.MOD_UPDATE, this::updateModUpdateJobSettings,
            AswgJobNames.INSTALL_DELETE_MODS, this::updateInstallDeleteModsJobSettings,
            AswgJobNames.DIFFICULTY_SCAN, this::updateDifficultyScanJobSettings,
            AswgJobNames.MOD_SETTINGS_SCAN, this::updateModSettingsScanJobSettings,
            AswgJobNames.MISSIONS_SCANNER, this::updateMissionScanJobSettings
    );

    @EventListener
    public void startJobs(ApplicationReadyEvent event)
    {
        try
        {
            log.info("Starting jobs...");
            rescheduleJobIfEnabled(this.modUpdateJob,
                    this.aswgConfig.getJobsProperties().isJobModUpdateEnabled(),
                    this.aswgConfig.getJobsProperties().getJobModUpdateCron());
            rescheduleJobIfEnabled(this.installDeleteModsFromFilesystemJob,
                    this.aswgConfig.getJobsProperties().isModsScannerEnabled(),
                    this.aswgConfig.getJobsProperties().getModsScannerCron());
            rescheduleJobIfEnabled(this.difficultyScanJob,
                    this.aswgConfig.getJobsProperties().isDifficultyProfileScannerEnabled(),
                    this.aswgConfig.getJobsProperties().getDifficultyProfileScannerCron());
            rescheduleJobIfEnabled(this.modSettingsScanJob,
                    this.aswgConfig.getJobsProperties().isModSettingsScannerEnabled(),
                    this.aswgConfig.getJobsProperties().getModSettingsScannerCron());
            rescheduleJobIfEnabled(this.missionScannerJob,
                    this.aswgConfig.getJobsProperties().isMissionScannerEnabled(),
                    this.aswgConfig.getJobsProperties().getMissionScannerCron());
        }
        catch (Exception exception)
        {
            log.error("Could not start ASWG jobs.", exception);
        }
    }

    public Optional<OffsetDateTime> getNextExecution(String name)
    {
        return aswgTaskScheduler.getNextExecution(name)
                .map(instant -> OffsetDateTime.ofInstant(instant, ZoneId.systemDefault()));
    }

    private void rescheduleJobIfEnabled(AswgJob aswgJob, boolean enabled, String cron)
    {
        if (!enabled)
            return;
        this.aswgTaskScheduler.schedule(aswgJob, cron);
    }

    public JobSettings getJobSettings(String name)
    {
         return Optional.ofNullable(jobSettingsSuppliers.get(name))
                 .map(Supplier::get)
                 .orElseThrow(() -> new IllegalArgumentException("Could not find job with name: " + name));
    }

    public JobSettings updateJobSettings(String name,
                                         boolean enabled,
                                         String cron,
                                         Map<String, String> parameters)
    {
        JobSettingsUpdateParams updateParams = new JobSettingsUpdateParams(name, enabled, cron, parameters);
        Consumer<JobSettingsUpdateParams> jobSettingsUpdateConsumer = this.jobSettingsUpdateConsumers.get(name);
        if (jobSettingsUpdateConsumer == null)
            throw new IllegalArgumentException("Could not find job with name: " + name);

        jobSettingsUpdateConsumer.accept(updateParams);
        this.aswgConfig.saveToFile();
        return getJobSettings(name);
    }

    private JobSettings getModUpdateJobSettings()
    {
        return JobSettings.builder()
                .name(AswgJobNames.MOD_UPDATE)
                .enabled(this.aswgConfig.getJobsProperties().isJobModUpdateEnabled())
                .cron(this.aswgConfig.getJobsProperties().getJobModUpdateCron())
                .build();
    }

    private JobSettings getInstallDeleteModsJobSettings()
    {
        return JobSettings.builder()
                .name(AswgJobNames.INSTALL_DELETE_MODS)
                .enabled(this.aswgConfig.getJobsProperties().isModsScannerInstallationEnabled())
                .cron(this.aswgConfig.getJobsProperties().getModsScannerCron())
                .parameters(Map.of(
                        InstallDeleteModsFromFilesystemJob.DELETION_ENABLED_PROPERTY, JobSettings.JobParameter.builder()
                                        .name(InstallDeleteModsFromFilesystemJob.DELETION_ENABLED_PROPERTY)
                                        .description("Is automatic DB deletion of deleted file system mods enabled?")
                                        .value(String.valueOf(this.aswgConfig.getJobsProperties().isModsScannerDeletionEnabled()))
                                        .build(),
                        InstallDeleteModsFromFilesystemJob.INSTALLATION_ENABLED_PROPERTY,
                        JobSettings.JobParameter.builder()
                                .name(InstallDeleteModsFromFilesystemJob.INSTALLATION_ENABLED_PROPERTY)
                                .description("Is automatic installation of new detected file system mods enabled?")
                                .value(String.valueOf(this.aswgConfig.getJobsProperties().isModsScannerInstallationEnabled()))
                                .build()))
                .build();
    }

    private JobSettings getDifficultyScanJobSettings()
    {
        return JobSettings.builder()
                .name(AswgJobNames.DIFFICULTY_SCAN)
                .enabled(this.aswgConfig.getJobsProperties().isDifficultyProfileScannerEnabled())
                .cron(this.aswgConfig.getJobsProperties().getDifficultyProfileScannerCron())
                .build();
    }

    private JobSettings getModSettingsScanJobSettings()
    {
        return JobSettings.builder()
                .name(AswgJobNames.MOD_SETTINGS_SCAN)
                .enabled(this.aswgConfig.getJobsProperties().isModSettingsScannerEnabled())
                .cron(this.aswgConfig.getJobsProperties().getModSettingsScannerCron())
                .build();
    }

    private JobSettings getMissionScanJobSettings()
    {
        return JobSettings.builder()
                .name(AswgJobNames.MISSIONS_SCANNER)
                .enabled(this.aswgConfig.getJobsProperties().isMissionScannerEnabled())
                .cron(this.aswgConfig.getJobsProperties().getMissionScannerCron())
                .build();
    }

    private void updateModUpdateJobSettings(JobSettingsUpdateParams params)
    {
        this.aswgConfig.getJobsProperties().setJobModUpdateEnabled(params.enabled());
        this.aswgConfig.getJobsProperties().setJobModUpdateCron(params.cron());

        rescheduleJobIfEnabled(modUpdateJob, params.enabled(), params.cron());
    }

    private void updateInstallDeleteModsJobSettings(JobSettingsUpdateParams params)
    {
        this.aswgConfig.getJobsProperties().setModsScannerEnabled(params.enabled());
        this.aswgConfig.getJobsProperties().setModsScannerCron(params.cron());
        this.aswgConfig.getJobsProperties().setModsScannerInstallationEnabled(
                Optional.ofNullable(params.parameters().get(InstallDeleteModsFromFilesystemJob.INSTALLATION_ENABLED_PROPERTY))
                        .map(Boolean::parseBoolean)
                        .orElse(false)
        );
        this.aswgConfig.getJobsProperties().setModsScannerDeletionEnabled(
                Optional.ofNullable(params.parameters().get(InstallDeleteModsFromFilesystemJob.DELETION_ENABLED_PROPERTY))
                        .map(Boolean::parseBoolean)
                        .orElse(false)
        );

        rescheduleJobIfEnabled(installDeleteModsFromFilesystemJob, params.enabled(), params.cron());
    }

    private void updateDifficultyScanJobSettings(JobSettingsUpdateParams params)
    {
        this.aswgConfig.getJobsProperties().setDifficultyProfileScannerEnabled(params.enabled());
        this.aswgConfig.getJobsProperties().setDifficultyProfileScannerCron(params.cron());

        rescheduleJobIfEnabled(difficultyScanJob, params.enabled(), params.cron());
    }

    private void updateModSettingsScanJobSettings(JobSettingsUpdateParams params)
    {
        this.aswgConfig.getJobsProperties().setModSettingsScannerEnabled(params.enabled());
        this.aswgConfig.getJobsProperties().setModSettingsScannerCron(params.cron());

        rescheduleJobIfEnabled(modSettingsScanJob, params.enabled(), params.cron());
    }

    private void updateMissionScanJobSettings(JobSettingsUpdateParams params)
    {
        this.aswgConfig.getJobsProperties().setMissionScannerEnabled(params.enabled());
        this.aswgConfig.getJobsProperties().setMissionScannerCron(params.cron());

        rescheduleJobIfEnabled(missionScannerJob, params.enabled(), params.cron());
    }

    public Optional<OffsetDateTime> getLastExecutionDate(String name)
    {
        return this.jobExecutionInfoService.getLastExecutionDate(name);
    }

    public List<String> getJobsNames()
    {
        return List.of(
                AswgJobNames.MOD_UPDATE,
                AswgJobNames.INSTALL_DELETE_MODS,
                AswgJobNames.DIFFICULTY_SCAN,
                AswgJobNames.MOD_SETTINGS_SCAN,
                AswgJobNames.MISSIONS_SCANNER);
    }

    private record JobSettingsUpdateParams(String name, boolean enabled, String cron, Map<String, String> parameters)
    { }
}
