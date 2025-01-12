package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.util.AswgFileNameNormalizer;

@Component
@RequiredArgsConstructor
public class ModFolderNameHelper
{
    private final AswgFileNameNormalizer fileNameNormalizer;

    public String buildForWithoutExtension(FilePart filePart)
    {
        String fileName = buildFor(filePart);
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public String buildFor(FilePart multipartFile)
    {
        String modName = multipartFile.filename();
        if (!modName.startsWith("@"))
        {
            modName = "@" + modName;
        }
        return this.fileNameNormalizer.normalize(modName);
    }

    public String buildFor(String modName)
    {
        if (!modName.startsWith("@"))
        {
            modName = "@" + modName;
        }
        return this.fileNameNormalizer.normalize(modName);
    }
}
