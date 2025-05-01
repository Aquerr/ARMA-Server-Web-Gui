package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgWriteContext;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;

public class CfgQuotedStringParser implements CfgSimpleParser<String>
{
    @Override
    public <T> T parse(String text, Class<T> clazz) throws ParsingException
    {
        return (T) text.trim().substring(1, text.lastIndexOf("\""));
    }

    @Override
    public String parseToString(CfgWriteContext context, Object value) throws ParsingException
    {
        return String.format("\"%s\"", value);
    }
}
