package pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.CfgConfigReader;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.CfgReflectionUtil;

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
                    if (possibleClassProperty.startsWith("class"))
                    {
                        ArmaServerConfig.Missions.Mission mission = this.cfgMissionClassParser.parse(bufferedReader);
                        missions.getMissions().add(mission);
                        stringBuilder = new StringBuilder();
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
    public String parseToString(Object value)
    {
        if (value == null)
            return "";

        ArmaServerConfig.Missions missions = (ArmaServerConfig.Missions)value;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("class Missions\n")
                .append("{\n");

        for (final ArmaServerConfig.Missions.Mission mission : missions.getMissions())
        {
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

        Object value = CfgConfigReader.PARSERS.get(field.getAnnotation(CfgProperty.class).type()).parse(propertyValue);
        field.setAccessible(true);
        field.set(missions, value);
        field.setAccessible(false);
    }
}
