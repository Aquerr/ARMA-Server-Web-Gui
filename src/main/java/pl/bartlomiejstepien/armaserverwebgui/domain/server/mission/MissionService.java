package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Missions;

import java.util.List;

public interface MissionService
{
    void save(MultipartFile multipartFile, boolean overwrite);

    boolean checkMissionFileExists(String fileName);

    boolean deleteMission(String template);

    void saveEnabledMissionList(List<Mission> missions);

    Missions getMissions();

    void addMission(String name, String template);

    void updateMission(long id, Mission mission);
}
