package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgReadContext;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgWriteContext;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

public class CfgClassListParser
{

    public <T> Collection<T> parse(CfgReadContext context, Class<T> clazz) throws ParsingException
    {
        List<T> arrayList = new ArrayList<>();
        BufferedReader bufferedReader = context.getBufferedReader();

        try
        {
            while (bufferedReader.ready())
            {
                char character = (char) bufferedReader.read();
                if (character == '{')
                {
                    CfgParser cfgParser = CfgFileHandler.PARSERS.get(PropertyType.CLASS);

                    Object instance = cfgParser.parse(context, clazz);
                    arrayList.add((T) instance);
                }
            }
        }
        catch (Exception exception)
        {
            throw new ParsingException(exception);
        }

        return arrayList;
    }

    public String parseToString(CfgWriteContext context, Object collectionObject) throws ParsingException
    {
        Collection<?> collection = (Collection<?>) collectionObject;

        StringBuilder stringBuilder = new StringBuilder();

        for (Object object : collection)
        {
            stringBuilder.append(CfgFileHandler.PARSERS.get(PropertyType.CLASS).parseToString(context, object))
                    .append(";")
                    .append("\n");
        }

        return stringBuilder.toString();
    }
}
