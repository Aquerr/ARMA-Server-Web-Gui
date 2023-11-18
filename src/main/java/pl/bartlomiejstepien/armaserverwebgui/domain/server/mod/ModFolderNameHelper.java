package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

@Component
public class ModFolderNameHelper
{
    private static final String[] CHARACTERS_TO_REPLACE = {" ", "\\", "/", ":", "*", "?", "\"", "<", ">", "|"};
    private static final String REPLACEMENT = "_";

    public String buildFor(FilePart multipartFile)
    {
        String modName = multipartFile.filename();
        if (!modName.startsWith("@"))
        {
            modName = "@" + modName;
        }

        modName = toLowerCaseWithUncerscores(modName);
        return modName;
    }

    public String normalize(String modName)
    {
        return toLowerCaseWithUncerscores(modName);
    }

    private String toLowerCaseWithUncerscores(String modName)
    {
        modName = modName.toLowerCase();
        for (final String characterToReplace : CHARACTERS_TO_REPLACE)
        {
            modName = modName.replace(characterToReplace, REPLACEMENT);
        }
        return modName;
    }
}
