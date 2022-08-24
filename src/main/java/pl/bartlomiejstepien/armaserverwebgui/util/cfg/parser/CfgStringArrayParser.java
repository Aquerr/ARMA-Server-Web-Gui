package pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser;

import java.util.ArrayList;
import java.util.List;

public class CfgStringArrayParser implements CfgSimpleParser<String[]>
{
    @Override
    public String[] parse(String text)
    {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> strings = new ArrayList<>();
        boolean isString = false;
        for (char character : text.substring(1).toCharArray())
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

            if (('}' == character || ',' == character) && !isString)
            {
                strings.add(stringBuilder.toString().trim());
                stringBuilder.setLength(0);
                continue;
            }

            if (isString)
            {
                stringBuilder.append(character);
            }
        }
        return strings.toArray(new String[0]);
    }

    @Override
    public String parseToString(Object value)
    {
        String[] array = (String[]) value;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (String text : array)
        {
            stringBuilder.append("\"")
                    .append(text)
                    .append("\",");
        }
        if (stringBuilder.toString().endsWith(","))
        {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append("};");
        return stringBuilder.toString();
    }
}
