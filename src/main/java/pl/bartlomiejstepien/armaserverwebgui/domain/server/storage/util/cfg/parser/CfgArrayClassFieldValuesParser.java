package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgProperty;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CfgArrayClassFieldValuesParser<T> implements CfgSimpleParser<T[]>{

    @Override
    public String parseToString(T[] value)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (int i = 0; i < value.length; i++)
        {
            Object object = value[i];
            stringBuilder.append("\n\t")
                    .append(toPrimitiveObjectString(object));

            if (i < value.length - 1)
                stringBuilder.append(",");
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
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            try
            {
                CfgSimpleParser<T> cfgSimpleParser = (CfgSimpleParser<T>) CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
                field.setAccessible(true);
                String value = cfgSimpleParser.parseToString((T)field.get(object));
                field.setAccessible(false);
                stringBuilder.append(value);
                int lastIndexOfSemicolon = stringBuilder.lastIndexOf(";");
                if (lastIndexOfSemicolon == stringBuilder.length() - 1) // Remove semicolon ;
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);

                if (i < fields.length - 1)
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
    public T[] parse(String text, Class<T[]> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isString = false;
        boolean insideArray = false;
        boolean insideObject = false;
        List<T> objects = null;
        T currentObject = null;
        List<String> fieldValues = new ArrayList<>();
        for (char character : text.trim().toCharArray())
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

            if (isString)
            {
                stringBuilder.append(character);
                continue;
            }

            if (character == '{' && !insideArray)
            {
                insideArray = true;
                objects = new ArrayList<>();
                continue;
            }
            else if (character == '{' && !insideObject)
            {
                insideObject = true;
                currentObject = (T)clazz.getComponentType().getDeclaredConstructor().newInstance();
                continue;
            }

            if (character == '}' && insideObject)
            {
                fieldValues.add(stringBuilder.toString().trim());
                stringBuilder.setLength(0);
                insideObject = false;
                populateObjectFields(currentObject, fieldValues);
                fieldValues = new ArrayList<>();
                objects.add(currentObject);
                currentObject = null;
                continue;
            }

            if (',' == character)
            {
                if (insideObject)
                {
                    fieldValues.add(stringBuilder.toString().trim());
                    stringBuilder.setLength(0);
                }
                continue;
            }

            stringBuilder.append(character);
        }

        if (objects == null)
            return null;

        Object result = Array.newInstance(clazz.getComponentType(), objects.size());
        for (int i = 0; i < objects.size(); i++)
        {
            Array.set(result, i, objects.get(i));
        }

        return (T[])result;
    }

    @Override
    public T[] parse(String text)
    {
        return null;
    }

    private void populateObjectFields(T currentObject, List<String> fieldValues) throws IllegalAccessException
    {
        Field[] fields = currentObject.getClass().getDeclaredFields();
        for (int i = 0; i < fieldValues.size(); i++)
        {
            Field field = fields[i];
            CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
            if (cfgProperty == null)
                continue;

            Object value;
            if (field.getType().isPrimitive()) {
                value = PrimitiveParser.parse(fieldValues.get(i), field.getType());
            } else {
                CfgSimpleParser<?> cfgSimpleParser = (CfgSimpleParser<?>) CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
                if (cfgSimpleParser instanceof CfgQuotedStringParser) {
                    value = cfgSimpleParser.parse("\"" + fieldValues.get(i) + "\"");
                } else {
                    value = cfgSimpleParser.parse(fieldValues.get(i));
                }
            }

            field.setAccessible(true);
            field.set(currentObject, value);
            field.setAccessible(false);
        }
    }
}
