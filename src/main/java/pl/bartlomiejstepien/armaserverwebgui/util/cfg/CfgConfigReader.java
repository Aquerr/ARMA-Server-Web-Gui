package pl.bartlomiejstepien.armaserverwebgui.util.cfg;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser.CfgClassParser;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser.CfgIntegerParser;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser.CfgMissionsClassParser;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser.CfgParser;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser.CfgRawStringParser;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser.CfgStringArrayParser;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser.CfgQuotedStringParser;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.type.PropertyType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

@Component
public class CfgConfigReader
{
    public static final Map<PropertyType, CfgParser<?>> PARSERS = Map.of(
            PropertyType.QUOTED_STRING, new CfgQuotedStringParser(),
            PropertyType.RAW_STRING, new CfgRawStringParser(),
            PropertyType.INTEGER, new CfgIntegerParser(),
            PropertyType.STRING_ARRAY, new CfgStringArrayParser()
    );

    public static final Map<PropertyType, CfgClassParser<?>> classParsers = Map.of(
            PropertyType.MISSIONS, new CfgMissionsClassParser()
    );

    public ArmaServerConfig readConfig(String filePath)
    {
        File file = new File(filePath);
        if (!file.exists())
            return new ArmaServerConfig();

        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        Field[] declaredFields = ArmaServerConfig.class.getDeclaredFields();

        StringBuilder stringBuilder = new StringBuilder();

        boolean lastSymbolSlash = false;
        boolean isComment = false;
        boolean isString = false;
        boolean endValue = false;

        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader))
        {
            while (bufferedReader.ready())
            {
                char character = (char)bufferedReader.read();

                if ('\n' == character)
                {
                    isComment = false;
                    lastSymbolSlash = false;
                    continue;
                }

                if ('\t' == character)
                {
                    continue;
                }

                if (isComment)
                {
                    continue;
                }

                if ('/' == character)
                {
                    if (isString)
                    {
                        continue;
                    }

                    if (lastSymbolSlash)
                    {
                        isComment = true;
                        continue;
                    }
                    else
                    {
                        lastSymbolSlash = true;
                        continue;
                    }
                }

                stringBuilder.append(character);

                if ('\"' == character)
                {
                    if (!lastSymbolSlash)
                    {
                        if (isString)
                        {
                            isString = false;
                        }
                        else
                        {
                            isString = true;
                        }
                    }
                    continue;
                }

                if (';' == character)
                {
                    if (!isString)
                    {
                        endValue = true;
                    }
                }

                if ('{' == character)
                {
                    String possibleClassProperty = stringBuilder.toString();
                    if (possibleClassProperty.startsWith("class"))
                    {
                        String classPropertyName = possibleClassProperty.substring(5, possibleClassProperty.length() - 1).trim();
                        parseClassProperty(armaServerConfig, classPropertyName, bufferedReader, declaredFields);
                        stringBuilder.setLength(0);
                    }

                    continue;
                }

                if ('}' == character)
                {
                    continue;
                }

                if (endValue)
                {
                    parseProperty(armaServerConfig, declaredFields, stringBuilder.toString());
                    endValue = false;
                    stringBuilder.setLength(0);
                }
            }
        }
        catch (IOException | IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return armaServerConfig;
    }

    private static void parseClassProperty(ArmaServerConfig armaServerConfig, String classPropertyName, BufferedReader bufferedReader, Field[] declaredFields) throws IOException, IllegalAccessException
    {
        Field field = findFieldForPropertyName(declaredFields, classPropertyName);
        Object object = classParsers.get(field.getAnnotation(CfgProperty.class).type()).parse(bufferedReader);
        field.setAccessible(true);
        field.set(armaServerConfig, object);
        field.setAccessible(false);
    }

    private static void parseProperty(ArmaServerConfig armaServerConfig, Field[] fields, String property) throws IllegalAccessException
    {
        String propertyName = property.substring(0, property.indexOf("=")).trim();
        String propertyValue = property.substring(property.indexOf("=") + 1).trim();
        Field field = findFieldForPropertyName(fields, propertyName);
        Object value = PARSERS.get(field.getAnnotation(CfgProperty.class).type()).parse(propertyValue);
        field.setAccessible(true);
        field.set(armaServerConfig, value);
        field.setAccessible(false);
    }

    private static Field findFieldForPropertyName(Field[] fields, String propertyName)
    {
        for (final Field field : fields)
        {
            CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
            if (cfgProperty.name().equals(propertyName))
                return field;
        }
        return null;
    }

    public void saveConfig(ArmaServerConfig armaServerConfig)
    {

    }
}
