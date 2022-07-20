package pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser;

public class CfgQuotedStringParser implements CfgParser<String>
{
    @Override
    public String parse(String text)
    {
        return text.trim().substring(1, text.lastIndexOf("\";"));
    }
}
