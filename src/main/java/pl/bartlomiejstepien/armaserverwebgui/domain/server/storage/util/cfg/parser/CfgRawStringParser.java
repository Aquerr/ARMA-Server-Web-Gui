package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;

import java.lang.reflect.Field;

public class CfgRawStringParser implements CfgSimpleParser<String>
{
    @Override
    public <T> T parse(String input, Class<T> clazz) throws ParsingException
    {
        return (T)input.trim();
    }

    @Override
    public String parseToString(Field field, Object value)
    {
        return String.valueOf(value);
    }
}
