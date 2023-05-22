package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

public interface CfgParser<T, V>
{
    T parse(V value);

    String parseToString(Object value);
}
