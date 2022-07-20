package pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser;

public interface CfgParser<T>
{
    T parse(String text);
}
