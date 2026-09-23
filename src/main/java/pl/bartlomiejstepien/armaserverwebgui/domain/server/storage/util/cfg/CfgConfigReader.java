package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;

import java.io.File;
import java.io.IOException;

public interface CfgConfigReader
{

    <T> T readConfig(File file, Class<T> clazz) throws IOException, ParsingException;
}
