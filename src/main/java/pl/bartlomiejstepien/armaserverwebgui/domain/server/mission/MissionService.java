package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Missions;

public interface MissionService
{
    void save(MultipartFile multipartFile);

    boolean deleteMission(String template);

    void saveEnabledMissionList(List<Mission> missions);

    Missions getMissions();

    void addMission(String name, String template);

    void updateMission(long id, Mission mission);
}
