package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class CfgMissionParamsClassParser implements CfgClassParser<ArmaServerConfig.Missions.Mission.Params>
{

    @Override
    public ArmaServerConfig.Missions.Mission.Params parse(BufferedReader bufferedReader)
    {
        ArmaServerConfig.Missions.Mission.Params params = new ArmaServerConfig.Missions.Mission.Params();
        StringBuilder stringBuilder = new StringBuilder();
        boolean lastSymbolSlash = false;
        boolean isComment = false;
        boolean isString = false;
        boolean endValue = false;
        boolean insideParamsClass = true;
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
//                    String possibleClassProperty = stringBuilder.toString();
//                    if (possibleClassProperty.startsWith("class"))
//                    {
//                        String classPropertyName = possibleClassProperty.substring(5, possibleClassProperty.length() - 1).trim();
//                        Params params = parseParams(armaServerConfig, classPropertyName, bufferedReader);
//                        mission.setParams(params);
//                        stringBuilder = new StringBuilder();
//                    }

                    continue;
                }

                if (!insideParamsClass)
                {
                    break;
                }

                if ('}' == character)
                {
                    insideParamsClass = false;
                    continue;
                }

                if (endValue)
                {
                    parseProperty(params, stringBuilder.toString());
                    endValue = false;
                    stringBuilder = new StringBuilder();
                }
            }
        }
        catch (IOException | IllegalAccessException exception)
        {
            exception.printStackTrace();
        }
        return params;
    }

    @Override
    public String parseToString(ArmaServerConfig.Missions.Mission.Params value)
    {
        if (value == null)
        {
            return "class Params {};";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("class Params\n")
                .append("\t\t{");

        for (final Map.Entry<String, String> param : value.getParams().entrySet())
        {
            stringBuilder.append("\n\t\t\t");
            writeParamToStringBuilder(param.getKey(), param.getValue(), stringBuilder);
        }

        stringBuilder.append("\n\t\t};\n");
        return stringBuilder.toString();
    }

    private void writeParamToStringBuilder(String paramName, String paramValue, StringBuilder stringBuilder)
    {
            stringBuilder.append(paramName)
                    .append(" = ")
                    .append(paramValue)
                    .append(";");
    }

    private void parseProperty(ArmaServerConfig.Missions.Mission.Params params, String property) throws IllegalAccessException
    {
        String propertyName = property.substring(0, property.indexOf("=")).trim();
        String propertyValue = property.substring(property.indexOf("=") + 1).trim();
        CfgSimpleParser<String> cfgSimpleParser = (CfgSimpleParser<String>) CfgFileHandler.PARSERS.get(PropertyType.RAW_STRING);
        String parsedValue = cfgSimpleParser.parse(propertyValue);
        params.getParams().put(propertyName, parsedValue);
    }
}
