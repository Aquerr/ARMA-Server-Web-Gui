package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import lombok.extern.slf4j.Slf4j;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgReflectionUtil;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.ClassName;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class CfgClassParserImpl implements CfgClassParser
{
    @Override
    public <T> T parse(BufferedReader bufferedReader, Class<T> clazz) throws ParsingException
    {
        try
        {
            T instance = null;
            if (Collection.class.isAssignableFrom(clazz)) {
                instance = (T)new ArrayList<>();
            } else if (Map.class.isAssignableFrom(clazz)) {
                instance = (T)new HashMap<>();
            } else {
                instance = clazz.getDeclaredConstructor().newInstance();
            }

            StringBuilder stringBuilder = new StringBuilder();
            boolean lastSymbolSlash = false;
            boolean isComment = false;
            boolean isString = false;
            boolean endValue = false;
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
                    String possibleClassProperty = stringBuilder.toString().trim();
                    if (possibleClassProperty.startsWith("class"))
                    {
                        String classPropertyName = possibleClassProperty.substring(5, possibleClassProperty.length() - 1).trim();
                        parseClassProperty(instance, classPropertyName, bufferedReader);
                        stringBuilder.setLength(0);
                    }

                    continue;
                }

                if (endValue)
                {
                    parseProperty(instance, stringBuilder.toString());
                    endValue = false;
                    stringBuilder = new StringBuilder();
                }

            }

            return instance;
        }
        catch (Exception exception)
        {
            throw new ParsingException(exception);
        }
    }

    @Override
    public String parseToString(Field field, Object instance) throws ParsingException
    {
        if (instance == null)
            return "";

        try
        {
            Class<?> clazz = instance.getClass();

            StringBuilder stringBuilder = new StringBuilder();

            String className = getClassNameFromField(field);
            Field classNameField = CfgReflectionUtil.findFieldWithAnnotation(clazz, ClassName.class);
            if (classNameField != null)
            {
                className = getClassNameFromField(instance, classNameField);
            }

            stringBuilder
                    .append(resolveIndentation())
                    .append("class ")
                    .append(className)
                    .append("\n")
                    .append(resolveIndentation())
                    .append("{")
                    .append(resolveIndentation())
                    .append("\n");

            CfgFileHandler.INDENTATION++;

            if (Collection.class.isAssignableFrom(instance.getClass()))
            {
//                stringBuilder.append(new CfgClassListParser().parseToString(field, instance));

                Collection<?> collection = (Collection<?>) instance;
                for (Object object : collection)
                {
                    stringBuilder
                            .append(CfgFileHandler.PARSERS.get(PropertyType.CLASS).parseToString(field, object))
                            .append(";")
                            .append("\n");
                }
            }
            else if (Map.class.isAssignableFrom(instance.getClass()))
            {
                Map<?, ?> map = (Map<?, ?>) instance;
                for (Map.Entry<?, ?> mapEntry : map.entrySet())
                {
                    stringBuilder.append(mapEntry.getKey())
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
                    stringBuilder.append(resolveIndentation());
                    writeFieldValueToStringBuilder(instance, delcaredField, stringBuilder);
                }
            }

            CfgFileHandler.INDENTATION--;

            stringBuilder.append("\n")
                    .append(resolveIndentation())
                    .append("}");

            return stringBuilder.toString();
        }
        catch (Exception exception)
        {
            throw new ParsingException(exception);
        }
    }

    private String resolveIndentation()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < CfgFileHandler.INDENTATION; i++)
        {
            stringBuilder.append("\t");
        }
        return stringBuilder.toString();
    }

    private String getClassNameFromField(Field field) throws IllegalAccessException
    {
        return Optional.ofNullable(field.getAnnotation(CfgProperty.class))
                .map(CfgProperty::name)
                .orElse(field.getName())
                .replaceAll("\\.", "_")
                .replaceAll("-", "_");
    }

    private String getClassNameFromField(Object instance, Field field) throws IllegalAccessException
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

    private static <T> void parseClassProperty(T instance, String classPropertyName, BufferedReader bufferedReader)
    {
        try
        {
            Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(instance.getClass(), classPropertyName);
            if (field == null)
            {
                return;
            }

            if (Collection.class.isAssignableFrom(field.getType())) {

                new CfgClassListParser().parse(bufferedReader, (Class<? extends Object>) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
            }

            CfgParser<?> cfgParser = CfgFileHandler.PARSERS.get(field.getAnnotation(CfgProperty.class).type());
            CfgClassParser cfgClassParser = (CfgClassParser) cfgParser;
            Object object = cfgClassParser.parse(bufferedReader, field.getType());

            field.setAccessible(true);
            field.set(instance, object);
            field.setAccessible(false);
        }
        catch (Exception exception)
        {
            log.error("Error during parsing of class property: {}", classPropertyName, exception);
        }
    }

    private static <T> void parseProperty(T instance, String property)
    {
        try
        {
            String propertyName = property.substring(0, property.indexOf("=")).trim();
            String propertyValue = property.substring(property.indexOf("=") + 1, property.length() - 1).trim();
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
            log.error("Error during parsing of property: {}", property, exception);
        }
    }

    private <T> void writeFieldValueToStringBuilder(Object instance, Field field, StringBuilder stringBuilder) throws IllegalAccessException, ParsingException
    {
        CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
        field.setAccessible(true);
        Object fieldValue = field.get(instance);
        field.setAccessible(false);
        CfgParser<?> cfgParser = CfgFileHandler.PARSERS.get(cfgProperty.type());
        String fieldValueAsString = cfgParser.parseToString(field, fieldValue);

        if (cfgProperty.isClass())
        {
            stringBuilder.append(fieldValueAsString + ";\n");
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
