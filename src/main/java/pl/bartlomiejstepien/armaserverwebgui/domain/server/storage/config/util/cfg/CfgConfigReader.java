package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgClassParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgIntegerParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgMissionsClassParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgSimpleParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgRawStringParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgStringArrayParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgQuotedStringParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.type.PropertyType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

@Component
@Slf4j
public class CfgConfigReader
{
    public static final Map<PropertyType, CfgSimpleParser<?>> PARSERS = Map.of(
            PropertyType.QUOTED_STRING, new CfgQuotedStringParser(),
            PropertyType.RAW_STRING, new CfgRawStringParser(),
            PropertyType.INTEGER, new CfgIntegerParser(),
            PropertyType.STRING_ARRAY, new CfgStringArrayParser()
    );

    public static final Map<PropertyType, CfgClassParser<?>> classParsers = Map.of(
            PropertyType.MISSIONS, new CfgMissionsClassParser()
    );

    public ArmaServerConfig readConfig(File file)
    {
        if (!file.exists())
            return new ArmaServerConfig();

        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        Class<?> armaServerConfigClass = ArmaServerConfig.class;

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

                if ('\n' == character || '\r' == character)
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
                    String possibleClassProperty = stringBuilder.toString().trim();
                    if (possibleClassProperty.startsWith("class"))
                    {
                        String classPropertyName = possibleClassProperty.substring(5, possibleClassProperty.length() - 1).trim();
                        parseClassProperty(armaServerConfig, classPropertyName, bufferedReader, armaServerConfigClass);
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
                    parseProperty(armaServerConfig, armaServerConfigClass, stringBuilder.toString().trim());
                    endValue = false;
                    stringBuilder.setLength(0);
                }
            }
        }
        catch (IOException | IllegalAccessException e)
        {
            log.error("Error during parsing of config file.", e);
        }

        return armaServerConfig;
    }

    private static void parseClassProperty(ArmaServerConfig armaServerConfig, String classPropertyName, BufferedReader bufferedReader, Class<?> clazz)
    {
        try
        {
            Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(clazz, classPropertyName);
            Object object = classParsers.get(field.getAnnotation(CfgProperty.class).type()).parse(bufferedReader);
            field.setAccessible(true);
            field.set(armaServerConfig, object);
            field.setAccessible(false);
        }
        catch (Exception exception)
        {
            log.error("Error with class property: {}", classPropertyName, exception);
        }
    }

    private static void parseProperty(ArmaServerConfig armaServerConfig, Class<?> clazz, String property) throws IllegalAccessException
    {
        try
        {
            String propertyName = property.substring(0, property.indexOf("=")).trim();
            String propertyValue = property.substring(property.indexOf("=") + 1).trim();
            Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(clazz, propertyName);
            if (field == null)
            {
                return;
            }
            Object value = PARSERS.get(field.getAnnotation(CfgProperty.class).type()).parse(propertyValue);
            field.setAccessible(true);
            field.set(armaServerConfig, value);
            field.setAccessible(false);
        }
        catch (Exception exception)
        {
            log.error("Error with parsing property: {}", property, exception);
        }
    }
}
