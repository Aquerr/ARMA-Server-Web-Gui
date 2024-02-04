package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgReflectionUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;

public class CfgMissionsClassParser implements CfgClassParser<ArmaServerConfig.Missions>
{
    private final CfgMissionClassParser cfgMissionClassParser = new CfgMissionClassParser();

    @Override
    public ArmaServerConfig.Missions parse(BufferedReader bufferedReader)
    {
        ArmaServerConfig.Missions missions = new ArmaServerConfig.Missions();
        StringBuilder stringBuilder = new StringBuilder();
        boolean lastSymbolSlash = false;
        boolean isComment = false;
        boolean isString = false;
        boolean endValue = false;
        boolean insideMissionsClass = true;
        try
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
                    if (possibleClassProperty.trim().startsWith("class"))
                    {
                        ArmaServerConfig.Missions.Mission mission = this.cfgMissionClassParser.parse(bufferedReader);
                        missions.getMissions().add(mission);
                        stringBuilder.setLength(0);
                    }

                    continue;
                }

                if (!insideMissionsClass)
                {
                    break;
                }

                if ('}' == character)
                {
                    insideMissionsClass = false;
                    continue;
                }

                if (endValue)
                {
                    parseProperty(missions, stringBuilder.toString());
                    endValue = false;
                    stringBuilder = new StringBuilder();
                }
            }
        }
        catch (IOException | IllegalAccessException exception)
        {
            exception.printStackTrace();
        }
        return missions;
    }

    @Override
    public String parseToString(ArmaServerConfig.Missions value)
    {
        if (value == null)
            return "";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("class Missions\n")
                .append("{");

        for (final ArmaServerConfig.Missions.Mission mission : value.getMissions())
        {
            stringBuilder.append("\t");
            stringBuilder.append(cfgMissionClassParser.parseToString(mission));
        }

        stringBuilder.append("};\n");
        return stringBuilder.toString();
    }

    private void parseProperty(ArmaServerConfig.Missions missions, String property) throws IllegalAccessException
    {
        String propertyName = property.substring(0, property.indexOf("=")).trim();
        String propertyValue = property.substring(property.indexOf("=") + 1).trim();
        Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(ArmaServerConfig.Missions.class, propertyName);
        if (field == null)
            return;

        CfgSimpleParser<?> cfgSimpleParser = (CfgSimpleParser<?>) CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
        Object value = cfgSimpleParser.parse(propertyValue);
        field.setAccessible(true);
        field.set(missions, value);
        field.setAccessible(false);
    }
}
