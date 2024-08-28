package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgWriteContext;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CfgStringArrayParser implements CfgSimpleParser<String>
{
    @Override
    public <T> T parse(String input, Class<T> clazz) throws ParsingException
    {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> strings = new ArrayList<>();
        boolean isString = false;
        for (char character : input.substring(1).toCharArray())
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
                if (!stringBuilder.isEmpty()) {
                    strings.add(stringBuilder.toString().trim());
                }
                stringBuilder.setLength(0);
                continue;
            }

            if (isString)
            {
                stringBuilder.append(character);
            }
        }
        return (T)strings.toArray(new String[0]);
    }

    @Override
    public String parseToString(CfgWriteContext context, Object value) throws ParsingException
    {
        if (!value.getClass().isArray())
            throw new ParsingException("Provided value is not an array: " + value);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (int i = 0; i < Array.getLength(value); i++)
        {
            String text = String.valueOf(Array.get(value, i));
            stringBuilder.append("\n\t\"")
                    .append(text)
                    .append("\"");

            if (i < Array.getLength(value) - 1)
                stringBuilder.append(",");
        }

        if (Array.getLength(value) > 0) {
            stringBuilder.append("\n");
        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }
}
