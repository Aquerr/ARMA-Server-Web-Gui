package pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser;

import java.util.ArrayList;
import java.util.List;

public class CfgStringArrayParser implements CfgParser<String[]>
{
    @Override
    public String[] parse(String text)
    {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> strings = new ArrayList<>();
        boolean isString = false;
        for (char character : text.toCharArray())
        {
            if ('\n' == character || '\t' == character)
            {
                continue;
            }

            if ('\"' == character)
            {
                isString = !isString;
                continue;
            }

            if (',' == character && !isString)
            {
                strings.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            }

            stringBuilder.append(character);
        }
        return strings.toArray(new String[0]);
    }
}
