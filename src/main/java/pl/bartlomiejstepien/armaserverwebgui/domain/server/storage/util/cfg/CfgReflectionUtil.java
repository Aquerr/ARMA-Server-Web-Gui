package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;

public class CfgReflectionUtil
{
    public static Field findClassFieldForCfgConfigProperty(Class<?> clazz, String propertyName)
    {
        for (final Field field : clazz.getDeclaredFields())
        {
            CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
            if (cfgProperty == null)
                continue;

            if (cfgProperty.name().equals(propertyName))
                return field;
        }
        return null;
    }

    public static Field findFieldWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass)
    {
        for (final Field field : clazz.getDeclaredFields())
        {
            if (field.isAnnotationPresent(annotationClass))
                return field;
        }
        return null;
    }

    public static List<Field> findAllCfgProperties(Class<?> clazz)
    {
        return findAllFieldsWithAnnotation(clazz, CfgProperty.class);
    }

    public static List<Field> findAllFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation)
    {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotation))
                .toList();
    }
}
