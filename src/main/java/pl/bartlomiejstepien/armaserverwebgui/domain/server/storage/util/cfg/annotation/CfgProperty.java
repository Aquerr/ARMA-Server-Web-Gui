package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CfgProperty
{
    String name();

    PropertyType type();

    boolean isClass() default false;

    boolean skipIfNull() default true;
}
