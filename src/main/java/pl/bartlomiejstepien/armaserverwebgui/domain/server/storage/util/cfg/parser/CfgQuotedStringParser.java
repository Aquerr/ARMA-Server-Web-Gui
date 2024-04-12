package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

public class CfgQuotedStringParser implements CfgSimpleParser<String>
{
    @Override
    public String parse(String text)
    {
        return text.trim().substring(1, text.lastIndexOf("\""));
    }

    @Override
    public String parseToString(String value)
    {
        return String.format("\"%s\"", value);
    }
}
