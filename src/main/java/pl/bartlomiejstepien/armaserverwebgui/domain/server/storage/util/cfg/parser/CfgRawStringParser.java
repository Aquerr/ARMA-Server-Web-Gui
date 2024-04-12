package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

public class CfgRawStringParser implements CfgSimpleParser<Object>
{
    @Override
    public String parse(String text)
    {
        return text.trim();
    }

    @Override
    public String parseToString(Object value)
    {
        return String.valueOf(value);
    }
}
