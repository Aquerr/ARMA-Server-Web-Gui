package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CfgClassListParser implements CfgSimpleParser<BufferedReader>
{

    @Override
    public <T> T parse(BufferedReader bufferedReader, Class<T> clazz) throws ParsingException
    {
        List<T> arrayList = new ArrayList<>();

        try
        {
            while (bufferedReader.ready())
            {
                char character = (char)bufferedReader.read();

                CfgParser cfgParser = CfgFileHandler.PARSERS.get(PropertyType.CLASS);
                Object instance = cfgParser.parse(bufferedReader, clazz);
                arrayList.add((T)instance);
            }
        }
        catch (Exception exception)
        {
            throw new ParsingException(exception);
        }

        return (T)arrayList;
    }

    @Override
    public String parseToString(Field field, Object collectionObject) throws ParsingException
    {
        Collection<?> collection = (Collection<?>) collectionObject;

        StringBuilder stringBuilder = new StringBuilder();

        for (Object object : collection)
        {
            stringBuilder.append(CfgFileHandler.PARSERS.get(PropertyType.CLASS).parseToString(field, object))
                    .append(";")
                    .append("\n");
        }

        return stringBuilder.toString();
    }
}
