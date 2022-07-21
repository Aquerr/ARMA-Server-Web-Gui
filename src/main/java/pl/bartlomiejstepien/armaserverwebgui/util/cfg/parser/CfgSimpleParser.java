package pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser;

public interface CfgSimpleParser<T> extends CfgParser<T, String>
{
    T parse(String text);
}
