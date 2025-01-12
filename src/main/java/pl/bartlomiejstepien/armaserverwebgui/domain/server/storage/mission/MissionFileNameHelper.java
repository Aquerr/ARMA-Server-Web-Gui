package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.util.AswgFileNameNormalizer;

import java.io.File;

@Component
@RequiredArgsConstructor
public class MissionFileNameHelper
{
    private static final String MISSION_FILE_EXTENSION = ".pbo";
    private final AswgFileNameNormalizer fileNameNormalizer;

    public String resolveMissionNameFromFilePart(FilePart filePart)
    {
        String normalizedFileName = normalizeFileName(filePart.filename());
        return normalizedFileName.substring(0, normalizedFileName.length() - MISSION_FILE_EXTENSION.length());
    }

    public String resolveMissionNameFromFile(File file)
    {
        String normalizedFileName = normalizeFileName(file.getName());
        return normalizedFileName.substring(0, normalizedFileName.length() - MISSION_FILE_EXTENSION.length());
    }

    /**
     * Resolves file name based on the give mission template.
     *
     * @param missionTemplate the mission template
     * @return the file name
     */
    public String resolveFileName(String missionTemplate)
    {
        String fileName = this.fileNameNormalizer.normalize(missionTemplate);
        if (!missionTemplate.endsWith(MISSION_FILE_EXTENSION))
            fileName = fileName + MISSION_FILE_EXTENSION;
        return fileName;
    }

    public boolean isMissionFile(File file)
    {
        return file.isFile() && file.getName().endsWith(MISSION_FILE_EXTENSION);
    }

    public String normalizeFileName(String fileName)
    {
        return this.fileNameNormalizer.normalize(fileName);
    }
}
