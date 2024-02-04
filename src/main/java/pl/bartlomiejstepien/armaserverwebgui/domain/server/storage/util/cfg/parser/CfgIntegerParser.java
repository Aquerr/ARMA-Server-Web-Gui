package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

public class CfgIntegerParser implements CfgSimpleParser<Integer>
{
    @Override
    public Integer parse(String text)
    {
        return Integer.valueOf(text.substring(0, text.length() - 1).trim());
    }

    @Override
    public String parseToString(Integer value)
    {
        return value + ";";
    }
}
