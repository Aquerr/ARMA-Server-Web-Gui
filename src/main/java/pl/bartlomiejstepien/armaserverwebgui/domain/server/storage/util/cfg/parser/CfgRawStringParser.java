package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

public class CfgRawStringParser implements CfgSimpleParser<String>
{
    @Override
    public String parse(String text)
    {
        return text.trim().substring(0, text.lastIndexOf(";"));
    }

    @Override
    public String parseToString(Object value)
    {
        return value + ";";
    }
}
