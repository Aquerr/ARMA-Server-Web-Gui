package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import java.io.File;

public interface CfgConfigWriter
{

    <T> void saveConfig(File file, T instance);
}
