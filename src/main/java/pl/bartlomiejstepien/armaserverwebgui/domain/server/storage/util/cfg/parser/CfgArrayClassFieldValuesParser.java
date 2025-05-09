package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgReflectionUtil;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgWriteContext;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CfgArrayClassFieldValuesParser<T> implements CfgSimpleParser<String>
{

    @Override
    public String parseToString(CfgWriteContext context, Object value) throws ParsingException
    {
        if (!value.getClass().isArray())
            throw new ParsingException("Provided value is not an array: " + value);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (int i = 0; i < Array.getLength(value); i++)
        {
            Object object = Array.get(value, i);
            stringBuilder.append("\n\t")
                    .append(toPrimitiveObjectString(context, object));

            if (i < Array.getLength(value) - 1)
                stringBuilder.append(",");
        }
        stringBuilder.append("\n}");
        return stringBuilder.toString();
    }

    private <T> String toPrimitiveObjectString(CfgWriteContext context, Object object) throws ParsingException
    {
        if (object == null)
            return "";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");

        List<Field> fields = CfgReflectionUtil.findAllCfgProperties(object.getClass());
        for (int i = 0; i < fields.size(); i++)
        {
            Field field = fields.get(i);
            try
            {
                CfgSimpleParser<T> cfgSimpleParser = (CfgSimpleParser<T>) CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
                field.setAccessible(true);
                String value = cfgSimpleParser.parseToString(context, field.get(object));
                field.setAccessible(false);
                stringBuilder.append(value);
                int lastIndexOfSemicolon = stringBuilder.lastIndexOf(";");
                if (lastIndexOfSemicolon == stringBuilder.length() - 1) // Remove semicolon ;
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);

                if (i < fields.size() - 1)
                    stringBuilder.append(",");
            }
            catch (IllegalAccessException e)
            {
                throw new ParsingException(e);
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
    public <T> T parse(String text, Class<T> clazz) throws ParsingException
    {
        try
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
                    currentObject = (T) clazz.getComponentType().getDeclaredConstructor().newInstance();
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

            return (T) result;
        }
        catch (Exception exception)
        {
            throw new ParsingException(exception);
        }
    }

    private void populateObjectFields(Object currentObject, List<String> fieldValues) throws IllegalAccessException, ParsingException
    {
        List<Field> fields = CfgReflectionUtil.findAllCfgProperties(currentObject.getClass());
        for (int i = 0; i < fieldValues.size(); i++)
        {
            Field field = fields.get(i);
            CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
            if (cfgProperty == null)
                continue;

            Object value;
            if (field.getType().isPrimitive())
            {
                value = PrimitiveParser.parse(fieldValues.get(i), field.getType());
            }
            else
            {
                CfgSimpleParser cfgSimpleParser = (CfgSimpleParser) CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
                if (cfgSimpleParser instanceof CfgQuotedStringParser)
                {
                    value = cfgSimpleParser.parse("\"" + fieldValues.get(i) + "\"", String.class);
                }
                else
                {
                    value = cfgSimpleParser.parse(fieldValues.get(i), String.class);
                }
            }

            field.setAccessible(true);
            field.set(currentObject, value);
            field.setAccessible(false);
        }
    }
}
