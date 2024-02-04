package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import lombok.extern.slf4j.Slf4j;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgClassParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgSimpleParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;

@Slf4j
public class DefaultCfgConfigReader implements CfgConfigReader
{
    @Override
    public <T> T readConfig(File file, Class<T> clazz) throws IOException
    {
        try
        {
            T instance = clazz.getDeclaredConstructor(null).newInstance(null);

            if (!file.exists())
                return instance;

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
                            parseClassProperty(instance, classPropertyName, bufferedReader, clazz);
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
                        parseProperty(instance, clazz, stringBuilder.toString().trim());
                        endValue = false;
                        stringBuilder.setLength(0);
                    }
                }
            }
            catch (IOException | IllegalAccessException e)
            {
                log.error("Error during parsing of cfg file.", e);
                throw e;
            }

            return instance;
        }
        catch (Exception exception)
        {
            throw new IOException(exception.getMessage());
        }
    }

    private static <T> void parseClassProperty(T instance, String classPropertyName, BufferedReader bufferedReader, Class<T> clazz)
    {
        try
        {
            Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(clazz, classPropertyName);
            CfgParser<?, ?> cfgParser = CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
            CfgClassParser<?> cfgClassParser = (CfgClassParser<?>) cfgParser;
            Object object = cfgClassParser.parse(bufferedReader);
            field.setAccessible(true);
            field.set(instance, object);
            field.setAccessible(false);
        }
        catch (Exception exception)
        {
            log.error("Error with class property: {}", classPropertyName, exception);
        }
    }

    private static <T> void parseProperty(T instance, Class<T> clazz, String property) throws IllegalAccessException
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
            CfgSimpleParser<?> cfgSimpleParser = (CfgSimpleParser<?>) CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
            Object value = cfgSimpleParser.parse(propertyValue);
            field.setAccessible(true);
            field.set(instance, value);
            field.setAccessible(false);
        }
        catch (Exception exception)
        {
            log.error("Error with parsing property: {}", property, exception);
        }
    }
}
