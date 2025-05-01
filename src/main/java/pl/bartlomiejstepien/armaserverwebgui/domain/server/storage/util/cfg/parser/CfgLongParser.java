package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgWriteContext;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;

public class CfgLongParser implements CfgSimpleParser<String>
{
    @Override
    public <T> T parse(String input, Class<T> clazz) throws ParsingException
    {
        return (T) Long.valueOf(input.trim());
    }

    @Override
    public String parseToString(CfgWriteContext context, Object value) throws ParsingException
    {
        return String.valueOf(value);
    }
}
