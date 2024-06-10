package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class DefaultCfgConfigWriter implements CfgConfigWriter
{
    @Override
    public <T> void saveConfig(File file, T instance)
    {
        // Go through each field and save it to file
        try(FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter))
        {
            List<Field> cfgPropertyFields = CfgReflectionUtil.findAllCfgProperties(instance.getClass());
            for (final Field field : cfgPropertyFields)
            {
                writeFieldInFile(instance, field, bufferedWriter);
            }
        }
        catch (IOException | IllegalAccessException | ParsingException e)
        {
            e.printStackTrace();
        }
        CfgFileHandler.INDENTATION = 0;
    }

    private <T> void writeFieldInFile(T instance, Field field, BufferedWriter bufferedWriter) throws IllegalAccessException, IOException, ParsingException
    {
        CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
        CfgParser<?> cfgParser = CfgFileHandler.PARSERS.get(cfgProperty.type());
        field.setAccessible(true);
        Object fieldValue = field.get(instance);
        field.setAccessible(false);

        String fieldValueAsString = fieldValue != null ? cfgParser.parseToString(field, fieldValue) : null;

        if (cfgProperty.skipIfNull() && fieldValueAsString == null)
            return;

        if (cfgProperty.isClass())
        {
            bufferedWriter.write(fieldValueAsString + ";");
        }
        else
        {
            bufferedWriter.write(cfgProperty.name() + " = " + fieldValueAsString + ";\n");
        }
    }
}
