package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod;

import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.exception.CouldNotReadModMetaFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgReflectionUtil;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgSimpleParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

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

    public static ModMetaFile forFile(Path metaFilePath) throws CouldNotReadModMetaFile
    {
        ModMetaFile modMetaFile = new ModMetaFile();
        try
        {
            List<String> lines = Files.readAllLines(metaFilePath);
            for (String line : lines)
            {
                String propertyName = line.substring(0, line.indexOf("=")).trim();
                String propertyValue = line.substring(line.indexOf("=") + 1, line.length() - 1).trim();
                Field field = CfgReflectionUtil.findClassFieldForCfgConfigProperty(ModMetaFile.class, propertyName);
                if (field != null)
                {
                    CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
                    CfgSimpleParser cfgSimpleParser = (CfgSimpleParser) CfgFileHandler.PARSERS.get(cfgProperty.type());
                    field.setAccessible(true);
                    field.set(modMetaFile, cfgSimpleParser.parse(propertyValue, field.getType()));
                    field.setAccessible(false);
                }
            }
        }
        catch (IOException | IllegalAccessException | ParsingException e)
        {
            throw new CouldNotReadModMetaFile(e);
        }
        return modMetaFile;
    }
}
