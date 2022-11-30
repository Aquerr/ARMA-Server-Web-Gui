package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser;

public interface CfgSimpleParser<T> extends CfgParser<T, String>
{
    T parse(String text);
}
