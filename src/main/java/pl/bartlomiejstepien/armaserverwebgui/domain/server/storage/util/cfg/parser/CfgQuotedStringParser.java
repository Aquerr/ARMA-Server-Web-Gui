package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;

import java.lang.reflect.Field;

public class CfgQuotedStringParser implements CfgSimpleParser<String>
{
    @Override
    public <T> T parse(String text, Class<T> clazz) throws ParsingException
    {
        return (T)text.trim().substring(1, text.lastIndexOf("\""));
    }

    @Override
    public String parseToString(Field field, Object value) throws ParsingException
    {
        return String.format("\"%s\"", value);
    }
}
