package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission.MissionStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MissionServiceImpl implements MissionService
{
    private final MissionStorage missionStorage;
    private final ServerConfigStorage serverConfigStorage;

    @Override
    public Mono<Void> save(FilePart multipartFile)
    {
        if (missionStorage.doesMissionExists(multipartFile.filename()))
            throw new MissionFileAlreadyExistsException();

        try
        {
            return missionStorage.save(multipartFile);
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public List<String> getInstalledMissionNames()
    {
        return this.missionStorage.getInstalledMissionNames();
    }

    @Override
    public boolean deleteMission(String missionName)
    {
        List<Mission> missions = getMissions().getEnabledMissions().stream()
                .filter(mission -> !mission.getName().equals(missionName))
                .toList();
        saveEnabledMissionList(missions);
        return this.missionStorage.deleteMission(missionName);
    }

    @Override
    public void saveEnabledMissionList(List<Mission> missions)
    {
        ArmaServerConfig armaServerConfig = this.serverConfigStorage.getServerConfig();
        armaServerConfig.setMissions(new ArrayList<>(missions.stream()
                .map(this::convertToArmaMissionObject)
                .toList()));
        this.serverConfigStorage.saveServerConfig(armaServerConfig);
    }

    @Override
    public Missions getMissions()
    {
        List<String> installedMissionsNames = getInstalledMissionNames();
        List<Mission> enabledMissions = this.serverConfigStorage.getServerConfig().getMissions().stream()
                .map(this::convertToDomainMission)
                .toList();
        Missions missions = new Missions();
        missions.setEnabledMissions(enabledMissions);
        missions.setDisabledMissions(installedMissionsNames.stream()
                .filter(mission -> enabledMissions.stream().noneMatch(mission1 -> mission1.getName().equals(mission)))
                .map(missionName -> new Mission(missionName, Mission.Difficulty.REGULAR, Collections.emptySet()))
                .toList());

        return missions;
    }

    @Override
    public Mono<ResponseEntity<?>> addMission(String template)
    {
        //TODO: Put template mission in server.cfg.

        List<Mission> enabledMissions = new ArrayList<>(getMissions().getEnabledMissions());
        enabledMissions.add(new Mission(template, Mission.Difficulty.REGULAR, Collections.emptySet()));
        saveEnabledMissionList(enabledMissions);

        //TODO: Put template mission in database.
        return Mono.empty();
    }

    private Mission convertToDomainMission(ArmaServerConfig.Missions.Mission armaMission)
    {
        Mission mission = new Mission();
        mission.setName(armaMission.getTemplate());
        mission.setDifficulty(Mission.Difficulty.findOrDefault(Optional.ofNullable(armaMission.getDifficulty())
                .map(String::toUpperCase)
                .orElse(null)));
        mission.setParameters(convertToDomainMissionParameters(armaMission.getParams()));
        return mission;
    }

    private Set<Mission.Parameter> convertToDomainMissionParameters(Map<String, String> params)
    {
        return params.entrySet().stream()
                .map(entry -> new Mission.Parameter(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    private ArmaServerConfig.Missions.Mission convertToArmaMissionObject(Mission mission)
    {
        ArmaServerConfig.Missions.Mission armaMission = new ArmaServerConfig.Missions.Mission();
        armaMission.setTemplate(mission.getName());
        armaMission.setDifficulty(mission.getDifficulty().name().toLowerCase());
        armaMission.setParams(convertToArmaMissionParams(mission.getParameters()));
        return armaMission;
    }

    private Map<String, String> convertToArmaMissionParams(Set<Mission.Parameter> parameters)
    {
        Map<String, String> paramsMap = parameters.stream().collect(Collectors.toMap(Mission.Parameter::getName, Mission.Parameter::getValue));
        return paramsMap;
    }
}
