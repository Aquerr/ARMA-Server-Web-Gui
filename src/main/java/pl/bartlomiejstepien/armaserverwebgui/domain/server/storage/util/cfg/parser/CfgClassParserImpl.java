package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgReadContext;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgReflectionUtil;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgWriteContext;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.ClassName;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

@Slf4j
public class CfgClassParserImpl implements CfgClassParser
{
    @Override
    public <T> T parse(CfgReadContext context, Class<T> clazz) throws ParsingException
    {
        BufferedReader bufferedReader = context.getBufferedReader();

        try
        {
            T instance = null;
            if (Collection.class.isAssignableFrom(clazz))
            {
                instance = (T) new ArrayList<>();
            }
            else if (Map.class.isAssignableFrom(clazz))
            {
                instance = (T) new HashMap<>();
            }
            else
            {
                instance = clazz.getDeclaredConstructor().newInstance();
            }

            StringBuilder stringBuilder = new StringBuilder();
            boolean lastSymbolSlash = false;
            boolean isComment = false;
            boolean isString = false;
            boolean endValue = false;
            while (bufferedReader.ready())
            {
                char character = (char) bufferedReader.read();

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

                context.getReadData().append(character);
                stringBuilder.append(character);

                if ('\"' == character)
                {
                    if (!lastSymbolSlash)
                    {
                        isString = !isString;
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
                        parseClassProperty(instance, classPropertyName, context);
                        stringBuilder.setLength(0);
                    }

                    continue;
                }

                if (stringBuilder.toString().equals("};"))
                {
                    break;
                }

                if (endValue)
                {
                    parseProperty(context, instance, stringBuilder.toString());
                    endValue = false;
                    stringBuilder = new StringBuilder();
                }

            }

            return instance;
        }
        catch (Exception exception)
        {
            throw new ParsingException(context.prepareErrorMessage(), exception);
        }
    }

    @Override
    public String parseToString(CfgWriteContext context, Object instance) throws ParsingException
    {
        if (instance == null)
            return "";

        try
        {
            Class<?> clazz = instance.getClass();

            StringBuilder stringBuilder = new StringBuilder();

            Field field = context.getCurrentField();

            String className = getClassNameFromField(field);
            Field classNameField = CfgReflectionUtil.findFieldWithAnnotation(clazz, ClassName.class);
            if (classNameField != null)
            {
                className = getClassNameFromField(instance, classNameField);
            }

            stringBuilder
                    .append(context.indentation())
                    .append("class ")
                    .append(className)
                    .append("\n")
                    .append(context.indentation())
                    .append("{")
                    .append(context.indentation())
                    .append("\n");


            context.incrementIndentation();

            if (Collection.class.isAssignableFrom(instance.getClass()))
            {
                Collection<?> collection = (Collection<?>) instance;
                for (Object object : collection)
                {
                    stringBuilder
                            .append(CfgFileHandler.PARSERS.get(PropertyType.CLASS).parseToString(context, object))
                            .append(";")
                            .append("\n");
                }
            }
            else if (Map.class.isAssignableFrom(instance.getClass()))
            {
                Map<?, ?> map = (Map<?, ?>) instance;
                for (Map.Entry<?, ?> mapEntry : map.entrySet())
                {
                    stringBuilder
                            .append(context.indentation())
                            .append(mapEntry.getKey())
                            .append(" = ")
                            .append(mapEntry.getValue())
                            .append(";\n");
                }
            }
            else
            {
                List<Field> declaredFields = CfgReflectionUtil.findAllCfgProperties(clazz);

                for (Field delcaredField : declaredFields)
                {
                    stringBuilder.append(context.indentation());
                    writeFieldValueToStringBuilder(new CfgWriteContext(context.getBufferedWriter(), instance, delcaredField, context.getIndentation()),
                            stringBuilder);
                }
            }

            context.decrementIndentation();

            stringBuilder.append("\n")
                    .append(context.indentation())
                    .append("}");

            return stringBuilder.toString();
        }
        catch (Exception exception)
        {
            throw new ParsingException(exception);
        }
    }

    private static String getClassNameFromField(Field field)
    {
        return Optional.ofNullable(field.getAnnotation(CfgProperty.class))
                .map(CfgProperty::name)
                .orElse(field.getName())
                .replaceAll("\\.", "_")
                .replaceAll("-", "_");
    }

    private static String getClassNameFromField(Object instance, Field field) throws IllegalAccessException
    {
        boolean canAccessField = field.canAccess(instance);
        if (!canAccessField)
        {
            field.setAccessible(true);
        }

        String fieldValue = String.valueOf(field.get(instance)).replaceAll("\\.", "_").replaceAll("-", "_");
        field.setAccessible(canAccessField);
        return fieldValue;
    }

    private static <T> void parseClassProperty(T instance, String classPropertyName, CfgReadContext context)
    {
        try
        {
            Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(instance.getClass(), classPropertyName);
            if (Collection.class.isAssignableFrom(instance.getClass()))
            {
                Collection collection = (Collection<?>) instance;

                var object = ((CfgClassParser) CfgFileHandler.PARSERS.get(PropertyType.CLASS)).parse(context, collection.getClass().arrayType());

                ((Collection) instance).add(object);
                return;
            }
            else if (field == null)
            {
                return;
            }

            if (Collection.class.isAssignableFrom(field.getType()))
            {
                Collection collection = CfgFileHandler.CLASS_LIST_PARSER.parse(
                        context,
                        (Class<? extends Object>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]
                );
                field.setAccessible(true);
                field.set(instance, collection);
                field.setAccessible(false);
                return;
            }
            else if (Map.class.isAssignableFrom(field.getType()))
            {
                Map map = ((CfgClassParser) CfgFileHandler.PARSERS.get(PropertyType.CLASS)).parse(context, Map.class);
                field.setAccessible(true);
                field.set(instance, map);
                field.setAccessible(false);
                return;
            }

            CfgParser<?> cfgParser = CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
            CfgClassParser cfgClassParser = (CfgClassParser) cfgParser;
            Object object = cfgClassParser.parse(context, field.getType());

            field.setAccessible(true);
            field.set(instance, object);
            field.setAccessible(false);
        }
        catch (Exception exception)
        {
            log.error("Could not parse class property: '{}', {}", classPropertyName, context.prepareErrorMessage(), exception);
        }
    }

    private static <T> void parseProperty(CfgReadContext context, T instance, String property)
    {
        try
        {
            String propertyName = property.substring(0, property.indexOf("=")).trim();
            String propertyValue = property.substring(property.indexOf("=") + 1, property.length() - 1).trim();


            if (Map.class.isAssignableFrom(instance.getClass()))
            {
                CfgSimpleParser cfgSimpleParser = (CfgSimpleParser<?>) CfgFileHandler.PARSERS.get(PropertyType.RAW_STRING);
                Object value = cfgSimpleParser.parse(propertyValue, String.class);
                ((Map) instance).put(propertyName, value);
                return;
            }

            Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(instance.getClass(), propertyName);
            if (field == null)
            {
                return;
            }
            CfgSimpleParser cfgSimpleParser = (CfgSimpleParser<?>) CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
            Object value = cfgSimpleParser.parse(propertyValue, field.getType());
            field.setAccessible(true);
            field.set(instance, value);
            field.setAccessible(false);
        }
        catch (Exception exception)
        {
            log.error("Could not parse property: '{}', {}", property, context.prepareErrorMessage(), exception);
        }
    }

    private void writeFieldValueToStringBuilder(CfgWriteContext context, StringBuilder stringBuilder) throws IllegalAccessException, ParsingException
    {
        Field field = context.getCurrentField();
        Object instance = context.getInstance();
        CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
        field.setAccessible(true);
        Object fieldValue = field.get(instance);
        field.setAccessible(false);
        CfgParser<?> cfgParser = CfgFileHandler.PARSERS.get(cfgProperty.type());
        String fieldValueAsString = cfgParser.parseToString(context, fieldValue);

        if (cfgProperty.isClass())
        {
            stringBuilder.append(fieldValueAsString)
                    .append(";\n");
        }
        else
        {
            stringBuilder.append(cfgProperty.name())
                    .append(" = ")
                    .append(fieldValueAsString)
                    .append(";\n");
        }
    }
}
