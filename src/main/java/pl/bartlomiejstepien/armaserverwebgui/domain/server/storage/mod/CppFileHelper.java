package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import lombok.extern.slf4j.Slf4j;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.DefaultCfgConfigReader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

@Slf4j
public class CppFileHelper
{
    public static final String META_CPP = "meta.cpp";
    public static final String MOD_CPP = "mod.cpp";

    public static <T extends CppFile> T readFile(Path filePath, Class<?> clazz)
    {
        T instance = null;
        try
        {
            instance = (T)clazz.getDeclaredConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            log.warn("Could not read mod file: {}", filePath, e);
        }

        try
        {
            return (T) DefaultCfgConfigReader.INSTNACE.readConfig(filePath.toFile(), clazz);
        }
        catch (IOException e)
        {
            log.warn("Could not read mod file: {}", filePath, e);
        }
        return instance;
    }
}
