package pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser;

public class CfgRawStringParser implements CfgParser<String>
{
    @Override
    public String parse(String text)
    {
        return text.trim().substring(0, text.lastIndexOf(";"));
    }
}
