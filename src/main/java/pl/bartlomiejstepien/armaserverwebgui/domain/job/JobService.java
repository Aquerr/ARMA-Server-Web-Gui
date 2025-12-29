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
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.dto.JobExecution;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.model.JobSettings;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job.InstallDeleteModsFromFilesystemJob;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private final Map<String, AswgJob> aswgJobsMap;

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
            rescheduleJobIfEnabled(aswgJobsMap.get(AswgJobNames.MOD_UPDATE),
                    this.aswgConfig.getJobsProperties().isJobModUpdateEnabled(),
                    this.aswgConfig.getJobsProperties().getJobModUpdateCron());
            rescheduleJobIfEnabled(aswgJobsMap.get(AswgJobNames.INSTALL_DELETE_MODS),
                    this.aswgConfig.getJobsProperties().isModsScannerEnabled(),
                    this.aswgConfig.getJobsProperties().getModsScannerCron());
            rescheduleJobIfEnabled(aswgJobsMap.get(AswgJobNames.DIFFICULTY_SCAN),
                    this.aswgConfig.getJobsProperties().isDifficultyProfileScannerEnabled(),
                    this.aswgConfig.getJobsProperties().getDifficultyProfileScannerCron());
            rescheduleJobIfEnabled(aswgJobsMap.get(AswgJobNames.MOD_SETTINGS_SCAN),
                    this.aswgConfig.getJobsProperties().isModSettingsScannerEnabled(),
                    this.aswgConfig.getJobsProperties().getModSettingsScannerCron());
            rescheduleJobIfEnabled(aswgJobsMap.get(AswgJobNames.MISSIONS_SCANNER),
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

    public boolean runNow(String jobName)
    {
        AswgJob aswgJob = aswgJobsMap.get(jobName);
        if (aswgJob == null)
        {
            log.error("Job '{}' not found!", jobName);
            return false;
        }
        this.aswgTaskScheduler.runNow(aswgJob);
        return true;
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
                .enabled(this.aswgConfig.getJobsProperties().isModsScannerEnabled())
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

        rescheduleJobIfEnabled(aswgJobsMap.get(AswgJobNames.MOD_UPDATE), params.enabled(), params.cron());
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

        rescheduleJobIfEnabled(aswgJobsMap.get(AswgJobNames.INSTALL_DELETE_MODS), params.enabled(), params.cron());
    }

    private void updateDifficultyScanJobSettings(JobSettingsUpdateParams params)
    {
        this.aswgConfig.getJobsProperties().setDifficultyProfileScannerEnabled(params.enabled());
        this.aswgConfig.getJobsProperties().setDifficultyProfileScannerCron(params.cron());

        rescheduleJobIfEnabled(aswgJobsMap.get(AswgJobNames.DIFFICULTY_SCAN), params.enabled(), params.cron());
    }

    private void updateModSettingsScanJobSettings(JobSettingsUpdateParams params)
    {
        this.aswgConfig.getJobsProperties().setModSettingsScannerEnabled(params.enabled());
        this.aswgConfig.getJobsProperties().setModSettingsScannerCron(params.cron());

        rescheduleJobIfEnabled(aswgJobsMap.get(AswgJobNames.MOD_SETTINGS_SCAN), params.enabled(), params.cron());
    }

    private void updateMissionScanJobSettings(JobSettingsUpdateParams params)
    {
        this.aswgConfig.getJobsProperties().setMissionScannerEnabled(params.enabled());
        this.aswgConfig.getJobsProperties().setMissionScannerCron(params.cron());

        rescheduleJobIfEnabled(aswgJobsMap.get(AswgJobNames.MISSIONS_SCANNER), params.enabled(), params.cron());
    }

    public Optional<JobExecution> getLastJobExecution(String name)
    {
        return this.jobExecutionInfoService.getLastJobExecution(name);
    }

    public Set<String> getJobsNames()
    {
        return aswgJobsMap.keySet();
    }

    private record JobSettingsUpdateParams(String name, boolean enabled, String cron, Map<String, String> parameters)
    {
    }
}
