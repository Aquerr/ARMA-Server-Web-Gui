package pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority(T(pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority).MOD_PRESETS_VIEW.getCode())")
public @interface HasPermissionModPresetsView
{
}