package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.type.PropertyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CfgProperty
{
    String name();

    PropertyType type();

    boolean isClass() default false;
}
