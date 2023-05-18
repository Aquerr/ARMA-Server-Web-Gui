package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.mod;

import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.CfgConfigReader;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.CfgReflectionUtil;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.type.PropertyType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Data
public final class ModMetaFile
{
    @CfgProperty(name = "publishedid", type = PropertyType.LONG)
    private long publishedFileId;
    @CfgProperty(name = "name", type = PropertyType.QUOTED_STRING)
    private String name;

    private ModMetaFile()
    {

    }

    public static ModMetaFile forFile(Path metaFilePath)
    {
        ModMetaFile modMetaFile = new ModMetaFile();
        try
        {
            List<String> lines = Files.readAllLines(metaFilePath);
            for (String line : lines)
            {
                String propertyName = line.substring(0, line.indexOf("=")).trim();
                String propertyValue = line.substring(line.indexOf("=") + 1).trim();
                Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(ModMetaFile.class, propertyName);
                if (field != null)
                {
                    CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
                    field.setAccessible(true);
                    field.set(modMetaFile, CfgConfigReader.PARSERS.get(cfgProperty.type()).parse(propertyValue));
                    field.setAccessible(false);
                }
            }
        }
        catch (IOException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        return modMetaFile;
    }
}
