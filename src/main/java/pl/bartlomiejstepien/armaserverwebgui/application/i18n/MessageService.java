package pl.bartlomiejstepien.armaserverwebgui.application.i18n;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@AllArgsConstructor
@Slf4j
public class MessageService
{
    private final MessageSource exceptionMessageSource;

    public String resolveExceptionMessage(Locale locale, String messageKey, String... args)
    {
        return exceptionMessageSource.getMessage(messageKey, args, locale);
    }

    public String resolveExceptionMessage(String messageKey, String... args)
    {
        return resolveExceptionMessage(Locale.ENGLISH, messageKey, args);
    }
}
