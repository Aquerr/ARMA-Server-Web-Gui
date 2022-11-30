package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import lombok.AllArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Missions;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.mission.MissionStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
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
    public void saveEnabledMissionList(List<String> missions)
    {
        ArmaServerConfig armaServerConfig = this.serverConfigStorage.getServerConfig();
        ArmaServerConfig.Missions missionsConfigObject = new ArmaServerConfig.Missions();
        armaServerConfig.setMissions(missionsConfigObject);

        missionsConfigObject.setMissions(missions.stream()
                .map(this::convertToMissionObject)
                .collect(Collectors.toList()));

        this.serverConfigStorage.saveServerConfig(armaServerConfig);
    }

    @Override
    public Missions getMissions()
    {
        List<String> installedMissions = getInstalledMissionNames();
        List<String> enabledMissions = this.serverConfigStorage.getServerConfig().getMissions().getMissions().stream()
                .map(ArmaServerConfig.Missions.Mission::getTemplate)
                .collect(Collectors.toList());
        Missions missions = new Missions();
        missions.setEnabledMissions(enabledMissions);
        missions.setDisabledMissions(installedMissions.stream()
                .filter(mission -> !enabledMissions.contains(mission))
                .collect(Collectors.toList()));

        return missions;
    }

    private ArmaServerConfig.Missions.Mission convertToMissionObject(String missionName)
    {
        ArmaServerConfig.Missions.Mission mission = new ArmaServerConfig.Missions.Mission();
        mission.setTemplate(missionName);
        mission.setDifficulty("regular");
        mission.setParams(new ArmaServerConfig.Missions.Mission.Params());
        return mission;
    }
}
