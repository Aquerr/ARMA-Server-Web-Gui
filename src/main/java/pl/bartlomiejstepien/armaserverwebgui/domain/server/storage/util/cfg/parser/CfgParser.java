package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgWriteContext;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;

public interface CfgParser<V>
{
    <T> T parse(V input, Class<T> clazz) throws ParsingException;

    String parseToString(CfgWriteContext context, Object value) throws ParsingException;
}
