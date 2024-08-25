package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import java.io.File;
import java.io.IOException;

public interface CfgConfigWriter
{

    <T> void saveConfig(File file, T instance) throws IOException;
}
