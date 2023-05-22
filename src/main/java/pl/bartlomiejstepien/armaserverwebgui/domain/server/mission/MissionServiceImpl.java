package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import lombok.AllArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission.MissionStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        return this.missionStorage.deleteMission(missionName);
    }

    @Override
    public void saveEnabledMissionList(List<Mission> missions)
    {
        ArmaServerConfig armaServerConfig = this.serverConfigStorage.getServerConfig();
        ArmaServerConfig.Missions missionsConfigObject = new ArmaServerConfig.Missions();
        armaServerConfig.setMissions(missionsConfigObject);

        missionsConfigObject.setMissions(missions.stream()
                .map(this::convertToArmaMissionObject)
                .collect(Collectors.toList()));

        this.serverConfigStorage.saveServerConfig(armaServerConfig);
    }

    @Override
    public Missions getMissions()
    {
        List<String> installedMissionsNames = getInstalledMissionNames();
        List<Mission> enabledMissions = this.serverConfigStorage.getServerConfig().getMissions().getMissions().stream()
                .map(this::convertToDomainMission)
                .collect(Collectors.toList());
        Missions missions = new Missions();
        missions.setEnabledMissions(enabledMissions);
        missions.setDisabledMissions(installedMissionsNames.stream()
                .filter(mission -> enabledMissions.stream().noneMatch(mission1 -> mission1.getName().equals(mission)))
                .map(missionName -> new Mission(missionName, Collections.emptySet()))
                .collect(Collectors.toList()));

        return missions;
    }

    private Mission convertToDomainMission(ArmaServerConfig.Missions.Mission armaMission)
    {
        Mission mission = new Mission();
        mission.setName(armaMission.getTemplate());
        mission.setParameters(convertToDomainMissionParameters(armaMission.getParams()));
        return mission;
    }

    private Set<Mission.Parameter> convertToDomainMissionParameters(ArmaServerConfig.Missions.Mission.Params params)
    {
        return params.getParams().entrySet().stream()
                .map(entry -> new Mission.Parameter(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    private ArmaServerConfig.Missions.Mission convertToArmaMissionObject(Mission mission)
    {
        ArmaServerConfig.Missions.Mission armaMission = new ArmaServerConfig.Missions.Mission();
        armaMission.setTemplate(mission.getName());
        armaMission.setDifficulty("regular");
        armaMission.setParams(convertToArmaMissionParams(mission.getParameters()));
        return armaMission;
    }

    private ArmaServerConfig.Missions.Mission.Params convertToArmaMissionParams(Set<Mission.Parameter> parameters)
    {
        ArmaServerConfig.Missions.Mission.Params params = new ArmaServerConfig.Missions.Mission.Params();
        Map<String, String> paramsMap = parameters.stream().collect(Collectors.toMap(Mission.Parameter::getName, Mission.Parameter::getValue));
        params.setParams(paramsMap);
        return params;
    }
}
