package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class MissionFileNameHelper
{
    private static final String MISSION_FILE_EXTENSION = ".pbo";

    public String resolveMissionNameFromFile(File file)
    {
        return file.getName().substring(0, file.getName().length() - MISSION_FILE_EXTENSION.length());
    }

    public String resolveFileName(String missionName)
    {
        if (missionName.endsWith(MISSION_FILE_EXTENSION))
            return missionName;
        return missionName + MISSION_FILE_EXTENSION;
    }

    public boolean isMissionFile(File file)
    {
        return file.isFile() && file.getName().endsWith(MISSION_FILE_EXTENSION);
    }
}
