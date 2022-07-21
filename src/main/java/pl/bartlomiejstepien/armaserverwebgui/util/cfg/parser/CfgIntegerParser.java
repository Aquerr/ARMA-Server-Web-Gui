package pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser;

public class CfgIntegerParser implements CfgSimpleParser<Integer>
{
    @Override
    public Integer parse(String text)
    {
        return Integer.valueOf(text.substring(0, text.length() - 1).trim());
    }

    @Override
    public String parseToString(Object value)
    {
        return value + ";";
    }
}
