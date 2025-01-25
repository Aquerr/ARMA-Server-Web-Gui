package pl.bartlomiejstepien.armaserverwebgui.application.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration(proxyBeanMethods = false)
public class LocaleConfig
{
    @Bean
    public MessageSource exceptionMessageSource()
    {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/exception_messages");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
