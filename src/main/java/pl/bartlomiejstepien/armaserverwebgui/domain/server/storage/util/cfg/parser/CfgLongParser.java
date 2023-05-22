package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

public class CfgLongParser implements CfgSimpleParser<Long>
{
    @Override
    public Long parse(String text)
    {
        return Long.valueOf(text.substring(0, text.length() - 1).trim());
    }

    @Override
    public String parseToString(Object value)
    {
        return value + ";";
    }
}
