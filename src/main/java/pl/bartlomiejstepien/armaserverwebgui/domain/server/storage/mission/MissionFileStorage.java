package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MissionFileStorage
{
    void save(MultipartFile multipartFile) throws IOException;

    boolean doesMissionExists(String filename);

    List<String> getInstalledMissionTemplates();

    boolean deleteMission(String template);
}
