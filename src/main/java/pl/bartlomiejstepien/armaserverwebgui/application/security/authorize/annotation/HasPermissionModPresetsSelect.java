package pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority(T(pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority).MOD_PRESETS_SELECT.getCode())")
public @interface HasPermissionModPresetsSelect
{
}
