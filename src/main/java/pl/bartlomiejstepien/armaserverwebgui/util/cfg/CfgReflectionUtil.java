package pl.bartlomiejstepien.armaserverwebgui.util.cfg;

import java.lang.reflect.Field;

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
}
