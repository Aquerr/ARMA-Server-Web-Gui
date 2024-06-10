//package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;
//
//import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
//import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
//import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
//import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgReflectionUtil;
//import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.lang.reflect.Field;
//
//public class CfgMissionClassParser implements CfgClassParser<ArmaServerConfig.Missions.Mission>
//{
//    private final CfgMissionParamsClassParser cfgMissionParamsClassParser = new CfgMissionParamsClassParser();
//
//    @Override
//    public ArmaServerConfig.Missions.Mission parse(BufferedReader bufferedReader)
//    {
//        ArmaServerConfig.Missions.Mission mission = new ArmaServerConfig.Missions.Mission();
//        StringBuilder stringBuilder = new StringBuilder();
//        boolean lastSymbolSlash = false;
//        boolean isComment = false;
//        boolean isString = false;
//        boolean endValue = false;
//        boolean insideMissionClass = true;
//        try
//        {
//            while (bufferedReader.ready())
//            {
//                char character = (char)bufferedReader.read();
//
//                if ('\n' == character)
//                {
//                    isComment = false;
//                    lastSymbolSlash = false;
//                    continue;
//                }
//
//                if ('\t' == character)
//                {
//                    continue;
//                }
//
//                if (isComment)
//                {
//                    continue;
//                }
//
//                if ('/' == character)
//                {
//                    if (isString)
//                    {
//                        continue;
//                    }
//
//                    if (lastSymbolSlash)
//                    {
//                        isComment = true;
//                        continue;
//                    }
//                    else
//                    {
//                        lastSymbolSlash = true;
//                        continue;
//                    }
//                }
//
//                stringBuilder.append(character);
//
//                if ('\"' == character)
//                {
//                    if (!lastSymbolSlash)
//                    {
//                        if (isString)
//                        {
//                            isString = false;
//                        }
//                        else
//                        {
//                            isString = true;
//                        }
//                    }
//                    continue;
//                }
//
//                if (';' == character)
//                {
//                    if (!isString)
//                    {
//                        endValue = true;
//                    }
//                }
//
//                if ('{' == character)
//                {
//                    String possibleClassProperty = stringBuilder.toString();
//                    if (possibleClassProperty.trim().startsWith("class Params"))
//                    {
//                        ArmaServerConfig.Missions.Mission.Params params = this.cfgMissionParamsClassParser.parse(bufferedReader);
//                        mission.setParams(params);
//                        stringBuilder = new StringBuilder();
//                    }
//
//                    continue;
//                }
//
//                if (!insideMissionClass)
//                {
//                    break;
//                }
//
//                if ('}' == character)
//                {
//                    insideMissionClass = false;
//                    continue;
//                }
//
//                if (endValue)
//                {
//                    parseProperty(mission, stringBuilder.toString());
//                    endValue = false;
//                    stringBuilder = new StringBuilder();
//                }
//
//            }
//        }
//        catch (IOException | IllegalAccessException exception)
//        {
//            exception.printStackTrace();
//        }
//        return mission;
//    }
//
//    @Override
//    public String parseToString(ArmaServerConfig.Missions.Mission value)
//    {
//        if (value == null)
//            return "";
//
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("\n\tclass ")
//                .append(value.getTemplate().replaceAll("\\.", "_").replaceAll("-", "_"))
//                .append("\n\t{")
//                .append("\n");
//
//        Field[] declaredFields = ArmaServerConfig.Missions.Mission.class.getDeclaredFields();
//
//        for (Field field : declaredFields)
//        {
//            try
//            {
//                stringBuilder.append("\t\t");
//                writeFieldValueToStringBuilder(value, field, stringBuilder);
//            }
//            catch (IllegalAccessException | IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        stringBuilder.append("\n\t}\n");
//
//        return stringBuilder.toString();
//    }
//
//    private void parseProperty(ArmaServerConfig.Missions.Mission mission, String property) throws IllegalAccessException
//    {
//        String propertyName = property.substring(0, property.indexOf("=")).trim();
//        String propertyValue = property.substring(property.indexOf("=") + 1).trim();
//        Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(ArmaServerConfig.Missions.Mission.class, propertyName);
//        if (field == null)
//            return;
//
//        CfgSimpleParser<?> cfgSimpleParser = (CfgSimpleParser<?>) CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
//        Object value = cfgSimpleParser.parse(propertyValue);
//        field.setAccessible(true);
//        field.set(mission, value);
//        field.setAccessible(false);
//    }
//
//    private <T> void writeFieldValueToStringBuilder(ArmaServerConfig.Missions.Mission mission, Field field, StringBuilder stringBuilder) throws IllegalAccessException, IOException, ParsingException
//    {
//        CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
//        field.setAccessible(true);
//        Object fieldValue = field.get(mission);
//        field.setAccessible(false);
//        CfgParser<?> cfgParser = (CfgParser<?>) CfgFileHandler.PARSERS.get(cfgProperty.type());
//        String fieldValueAsString = cfgParser.parseToString(fieldValue);
//
//        if (cfgProperty.isClass())
//        {
//            stringBuilder.append(fieldValueAsString + ";");
//        }
//        else
//        {
//            stringBuilder.append(cfgProperty.name())
//                    .append(" = ")
//                    .append(fieldValueAsString)
//                    .append(";\n");
//        }
//    }
//}
