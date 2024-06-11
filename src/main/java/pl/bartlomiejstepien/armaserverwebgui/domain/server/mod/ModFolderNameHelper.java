package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

@Component
public class ModFolderNameHelper
{
    private static final String[] CHARACTERS_TO_REPLACE = {" ", "\\", "/", ":", "*", "?", "\"", "<", ">", "|"};
    private static final String REPLACEMENT = "_";


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
        return normalize(modName);
    }

    public String buildFor(String modName)
    {
        if (!modName.startsWith("@"))
        {
            modName = "@" + modName;
        }
        return normalize(modName);
    }

    public String normalize(String modName)
    {
        return toLowerCaseWithUnderscores(modName);
    }

    private String toLowerCaseWithUnderscores(String modName)
    {
        modName = modName.toLowerCase();
        for (final String characterToReplace : CHARACTERS_TO_REPLACE)
        {
            modName = modName.replace(characterToReplace, REPLACEMENT);
        }
        return modName;
    }
}
