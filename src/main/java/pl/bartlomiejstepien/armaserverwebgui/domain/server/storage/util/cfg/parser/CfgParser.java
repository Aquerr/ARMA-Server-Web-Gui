package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

public interface CfgParser<T, V>
{
    T parse(V input);

    default T parse(V input, Class<? extends T> returnType)
    {
        return parse(input);
    }

    String parseToString(T value);
}
