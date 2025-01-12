package pl.bartlomiejstepien.armaserverwebgui.application.util;

import org.springframework.stereotype.Component;

@Component
public class AswgFileNameNormalizer
{
    private static final String[] CHARACTERS_TO_REPLACE = {" ", "\\", "/", ":", "*", "?", "\"", "<", ">", "|"};
    private static final String REPLACEMENT = "_";

    public String normalize(String fileName)
    {
        return toLowerCaseWithUnderscores(fileName);
    }

    private String toLowerCaseWithUnderscores(String fileName)
    {
        fileName = fileName.toLowerCase();
        for (final String characterToReplace : CHARACTERS_TO_REPLACE)
        {
            fileName = fileName.replace(characterToReplace, REPLACEMENT);
        }
        return fileName;
    }
}
