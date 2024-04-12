package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class DefaultCfgConfigWriter implements CfgConfigWriter
{
    @Override
    public <T> void saveConfig(File file, T instance)
    {
        // Go through each field and save it to file
        try(FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter))
        {
            Field[] declaredFields = ArmaServerConfig.class.getDeclaredFields();
            for (final Field field : declaredFields)
            {
                writeFieldInFile(instance, field, bufferedWriter);
            }
        }
        catch (IOException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private <T, I> void writeFieldInFile(I instance, Field field, BufferedWriter bufferedWriter) throws IllegalAccessException, IOException
    {
        CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
        CfgParser<T, ?> cfgParser = (CfgParser<T, ?>) CfgFileHandler.PARSERS.get(cfgProperty.type());
        field.setAccessible(true);
        Object fieldValue = field.get(instance);
        field.setAccessible(false);

        String fieldValueAsString = fieldValue != null ? cfgParser.parseToString((T)fieldValue) : null;

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
