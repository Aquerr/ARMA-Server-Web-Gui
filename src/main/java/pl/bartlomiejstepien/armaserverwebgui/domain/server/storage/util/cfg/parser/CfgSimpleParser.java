package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import java.lang.reflect.InvocationTargetException;

public interface CfgSimpleParser<T> extends CfgParser<T, String>
{
    T parse(String text);

    default T parse(String text, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        return parse(text);
    }
}
