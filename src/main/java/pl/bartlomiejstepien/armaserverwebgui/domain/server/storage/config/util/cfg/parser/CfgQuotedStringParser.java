package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser;

public class CfgQuotedStringParser implements CfgSimpleParser<String>
{
    @Override
    public String parse(String text)
    {
        return text.trim().substring(1, text.lastIndexOf("\";"));
    }

    @Override
    public String parseToString(Object value)
    {
        return String.format("\"%s\";", value);
    }
}
