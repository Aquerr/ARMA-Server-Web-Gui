package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgReflectionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CfgArrayClassFieldValuesParser<T> implements CfgSimpleParser<T[]>{

    @Override
    public String parseToString(T[] value)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (Object object : value)
        {
            stringBuilder.append("\n\t\"")
                    .append(toPrimitiveObjectString(object))
                    .append("\",");
        }
        if (stringBuilder.toString().endsWith(","))
        {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append("\n};");
        return stringBuilder.toString();
    }

    private <T> String toPrimitiveObjectString(Object object)
    {
        if (object == null)
            return "";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields)
        {
            try
            {
                CfgSimpleParser<T> cfgSimpleParser = (CfgSimpleParser<T>) CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
                String value = cfgSimpleParser.parseToString((T)field.get(object));
                stringBuilder.append(value);
                stringBuilder.deleteCharAt(stringBuilder.length()); // Remove semicolon ;
                stringBuilder.append(",");
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }

        // Remove last comma
        if (stringBuilder.toString().endsWith(","))
        {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    @Override
    public T parse(String input, Class<T> returnType)
    {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> strings = new ArrayList<>();
        boolean isString = false;
        for (char character : text.substring(1).toCharArray())
        {
            if ('\n' == character || '\t' == character)
            {
                continue;
            }

            if ('\"' == character)
            {
                isString = !isString;
                continue;
            }

            if (('}' == character || ',' == character) && !isString)
            {
                strings.add(stringBuilder.toString().trim());
                stringBuilder.setLength(0);
                continue;
            }

            if (isString)
            {
                stringBuilder.append(character);
            }
        }
        return strings.toArray(new String[0]);
    }

    private void parseProperty(Object instance, String propertyName) throws IllegalAccessException
    {
        String propertyValue = property.substring(property.indexOf("=") + 1).trim();
        Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(instance.getClass(), propertyName);
        if (field == null)
            return;

        CfgSimpleParser<?> cfgSimpleParser = (CfgSimpleParser<?>) CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
        Object value = cfgSimpleParser.parse(propertyValue);
        field.setAccessible(true);
        field.set(instance, value);
        field.setAccessible(false);
    }
}
