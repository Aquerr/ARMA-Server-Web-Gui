package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

public class CfgIntegerParser implements CfgSimpleParser<Integer>
{
    @Override
    public Integer parse(String text)
    {
        return Integer.valueOf(text.trim());
    }

    @Override
    public String parseToString(Integer value)
    {
        return String.valueOf(value);
    }
}
