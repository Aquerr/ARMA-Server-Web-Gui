package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

public class CfgLongParser implements CfgSimpleParser<Long>
{
    @Override
    public Long parse(String text)
    {
        return Long.valueOf(text.trim());
    }

    @Override
    public String parseToString(Long value)
    {
        return String.valueOf(value);
    }
}
