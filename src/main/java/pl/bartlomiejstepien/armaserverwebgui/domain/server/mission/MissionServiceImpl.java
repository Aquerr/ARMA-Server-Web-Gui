package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.converter.MissionConverter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Missions;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionNotFoundException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model.MissionEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission.MissionFileNameHelper;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission.MissionFileStorage;
import pl.bartlomiejstepien.armaserverwebgui.repository.MissionRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class MissionServiceImpl implements MissionService
{
    private final MissionFileStorage missionFileStorage;
    private final MissionRepository missionRepository;
    private final ServerConfigStorage serverConfigStorage;
    private final MissionConverter missionConverter;
    private final MissionFileNameHelper missionFileNameHelper;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void missionScan()
    {
        log.info("Scanning for new file missions...");
        List<String> installedMissionTemplates = this.missionFileStorage.getInstalledMissionTemplates();
        List<String> notInstalledTemplates = this.findNotInstalledTemplates(installedMissionTemplates, this.missionRepository.findAll());

        log.info("Installing new missions: {}", notInstalledTemplates);
        for (String templateName : notInstalledTemplates)
        {
            addMission(templateName, templateName);
        }
    }

    @Override
    public boolean checkMissionFileExists(String fileName)
    {
        return missionFileStorage.doesMissionExists(fileName);
    }

    @Transactional
    @Override
    public void save(MultipartFile multipartFile, boolean overwrite)
    {
        if (!overwrite && checkMissionFileExists(multipartFile.getOriginalFilename()))
            throw new MissionFileAlreadyExistsException();

        try
        {
            String missionTemplate = missionFileNameHelper.resolveMissionNameFromFilePart(multipartFile);
            missionFileStorage.save(multipartFile);

            if (!overwrite)
            {
                addMission(missionTemplate, missionTemplate);
            }
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    private List<String> findNotInstalledTemplates(List<String> installedTemplates, List<MissionEntity> entities)
    {
        List<String> templatesInDB = entities.stream().map(MissionEntity::getTemplate).toList();

        return installedTemplates.stream()
                .filter(template -> !templatesInDB.contains(template))
                .toList();
    }

    @Transactional
    @Override
    public boolean deleteMission(String template)
    {
        log.info("Deleting mission by template: {}", template);
        if (this.missionRepository.findByTemplate(template).isEmpty())
            throw new MissionNotFoundException("No mission exist for template: " + template);

        this.missionRepository.deleteFirstByTemplate(template);
        return this.missionFileStorage.deleteMission(template);
    }

    @Transactional
    @Override
    public void saveEnabledMissionList(List<Mission> missions)
    {
        this.missionRepository.disableAll();
        if (missions.isEmpty())
            return;

        this.missionRepository.updateAllByTemplateSetEnabled(missions.stream().map(Mission::getTemplate).toList());
        syncConfigMissions();
    }

    @Transactional(readOnly = true)
    @Override
    public Missions getMissions()
    {
        Map<Boolean, List<Mission>> groupedMissions = this.missionRepository.findAll().stream()
                .map(this.missionConverter::convertToDomainMission)
                .collect(Collectors.groupingBy(Mission::isEnabled));

        Missions missions = new Missions();
        missions.setDisabledMissions(groupedMissions.getOrDefault(false, Collections.emptyList()));
        missions.setEnabledMissions(groupedMissions.getOrDefault(true, Collections.emptyList()));
        return missions;
    }

    @Transactional
    @Override
    public void addMission(String name, String template)
    {
        Mission mission = Mission.builder()
                .name(name)
                .template(template)
                .difficulty(Mission.Difficulty.REGULAR)
                .enabled(false)
                .parameters(Collections.emptySet())
                .build();

        this.missionRepository.save(missionConverter.convertToEntity(mission));
    }

    @Transactional
    @Override
    public void updateMission(long id, Mission mission)
    {
        this.missionRepository.findById(id).orElseThrow(() -> new MissionNotFoundException("Mission not found for id = " + id));
        missionRepository.save(missionConverter.convertToEntity(mission));
        syncConfigMissions();
    }

    private void syncConfigMissions()
    {
        List<Mission> missions = this.missionRepository.findAll().stream()
                .map(this.missionConverter::convertToDomainMission)
                .filter(Mission::isEnabled)
                .toList();

        ArmaServerConfig armaServerConfig = this.serverConfigStorage.getServerConfig();
        armaServerConfig.setMissions(new ArrayList<>(missions.stream()
                .map(this.missionConverter::convertToArmaMissionObject)
                .toList()));

        this.serverConfigStorage.saveServerConfig(armaServerConfig);
    }
}
