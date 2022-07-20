package pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.util.cfg.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.CfgConfigReader;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.CfgReflectionUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;

public class CfgMissionClassParser implements CfgClassParser<ArmaServerConfig.Missions.Mission>
{
    private final CfgMissionParamsClassParser cfgMissionParamsClassParser = new CfgMissionParamsClassParser();

    @Override
    public ArmaServerConfig.Missions.Mission parse(BufferedReader bufferedReader)
    {
        ArmaServerConfig.Missions.Mission mission = new ArmaServerConfig.Missions.Mission();
        StringBuilder stringBuilder = new StringBuilder();
        boolean lastSymbolSlash = false;
        boolean isComment = false;
        boolean isString = false;
        boolean endValue = false;
        boolean insideMissionClass = true;
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
                    if (possibleClassProperty.startsWith("class Params"))
                    {
                        ArmaServerConfig.Missions.Mission.Params params = this.cfgMissionParamsClassParser.parse(bufferedReader);
                        mission.setParams(params);
                        stringBuilder = new StringBuilder();
                    }

                    continue;
                }

                if (!insideMissionClass)
                {
                    break;
                }

                if ('}' == character)
                {
                    insideMissionClass = false;
                    continue;
                }

                if (endValue)
                {
                    parseProperty(mission, stringBuilder.toString());
                    endValue = false;
                    stringBuilder = new StringBuilder();
                }

            }
        }
        catch (IOException | IllegalAccessException exception)
        {
            exception.printStackTrace();
        }
        return mission;
    }

    private void parseProperty(ArmaServerConfig.Missions.Mission mission, String property) throws IllegalAccessException
    {
        String propertyName = property.substring(0, property.indexOf("=")).trim();
        String propertyValue = property.substring(property.indexOf("=") + 1).trim();
        Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(ArmaServerConfig.Missions.Mission.class, propertyName);
        if (field == null)
            return;

        Object value = CfgConfigReader.PARSERS.get(field.getAnnotation(CfgProperty.class).type()).parse(propertyValue);
        field.setAccessible(true);
        field.set(mission, value);
        field.setAccessible(false);
    }
}
