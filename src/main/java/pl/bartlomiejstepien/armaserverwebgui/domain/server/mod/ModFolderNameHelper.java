package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.application.util.AswgFileNameNormalizer;

@Component
@RequiredArgsConstructor
public class ModFolderNameHelper
{
    private final AswgFileNameNormalizer fileNameNormalizer;

    public String buildForWithoutExtension(MultipartFile multipartFile)
    {
        String fileName = buildFor(multipartFile);
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public String buildFor(MultipartFile multipartFile)
    {
        String modName = multipartFile.getOriginalFilename();
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
