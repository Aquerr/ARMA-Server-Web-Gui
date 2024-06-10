package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;

import java.lang.reflect.Field;

public interface CfgParser<V>
{
    <T> T parse(V input, Class<T> clazz) throws ParsingException;

    String parseToString(Field field, Object value) throws ParsingException;
}
