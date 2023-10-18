package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

@Component
public class ModFolderNameFactory
{
    public String buildFor(FilePart multipartFile)
    {
        String modName = multipartFile.filename();
        if (!modName.startsWith("@"))
        {
            modName = "@" + modName;
        }

        modName = toLowerCaseWithUncerscores(modName);

        if (modName.endsWith(".zip"))
        {
            modName = modName.substring(0, modName.lastIndexOf(".zip"));
        }
        return modName;
    }

    public String normalize(String modName)
    {
        return toLowerCaseWithUncerscores(modName);
    }

    private String toLowerCaseWithUncerscores(String modName)
    {
        return modName.toLowerCase().replace(" ", "_");
    }
}
