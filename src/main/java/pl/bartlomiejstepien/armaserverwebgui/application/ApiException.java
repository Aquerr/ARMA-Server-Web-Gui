package pl.bartlomiejstepien.armaserverwebgui.application;

import org.springframework.http.HttpStatus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiException
{
    HttpStatus status();

    ApiExceptionCode code();

    String messageKey();
}
