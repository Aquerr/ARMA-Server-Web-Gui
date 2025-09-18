package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.AswgJob;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.JobExecutionInfoService;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.AswgJobNames;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.MissionService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model.MissionEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission.MissionFileStorage;
import pl.bartlomiejstepien.armaserverwebgui.repository.MissionRepository;

import java.util.List;

@Slf4j
@Component
public class MissionScannerJob extends AswgJob
{
    private final MissionFileStorage missionFileStorage;
    private final MissionService missionService;
    private final MissionRepository missionRepository;

    @Autowired
    public MissionScannerJob(JobExecutionInfoService jobExecutionInfoService,
                             MissionFileStorage missionFileStorage,
                             MissionService missionService,
                             MissionRepository missionRepository)
    {
        super(jobExecutionInfoService);
        this.missionFileStorage = missionFileStorage;
        this.missionService = missionService;
        this.missionRepository = missionRepository;
    }

    @Override
    public String getName()
    {
        return AswgJobNames.MISSIONS_SCANNER;
    }

    @Override
    @Transactional
    public void runJob()
    {
        log.info("Scanning for new file missions...");
        List<String> installedMissionTemplates = this.missionFileStorage.getInstalledMissionTemplates();
        List<String> notInstalledTemplates = this.findNotInstalledTemplates(installedMissionTemplates, this.missionRepository.findAll());

        log.info("Installing new missions: {}", notInstalledTemplates);
        for (String templateName : notInstalledTemplates)
        {
            this.missionService.addMission(templateName, templateName);
        }
    }

    private List<String> findNotInstalledTemplates(List<String> installedTemplates, List<MissionEntity> entities)
    {
        List<String> templatesInDB = entities.stream().map(MissionEntity::getTemplate).toList();

        return installedTemplates.stream()
                .filter(template -> !templatesInDB.contains(template))
                .toList();
    }
}
