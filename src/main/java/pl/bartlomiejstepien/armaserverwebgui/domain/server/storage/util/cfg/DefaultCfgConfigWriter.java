package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import lombok.extern.slf4j.Slf4j;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;

@Slf4j
public class DefaultCfgConfigWriter implements CfgConfigWriter
{
    public static final DefaultCfgConfigWriter INSTANCE = new DefaultCfgConfigWriter();

    @Override
    public <T> void saveConfig(File file, T instance) throws IOException
    {
        // Go through each field and save it to file
        if (!file.exists())
        {
            Files.createDirectories(file.getParentFile().toPath());
            Files.createFile(file.toPath());
        }

        try (FileWriter fileWriter = new FileWriter(file, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter))
        {
            saveConfig(new CfgWriteContext(bufferedWriter, instance, null, 0));
        }
        catch (Exception exception)
        {
            log.error("Error during saving of cfg file.", exception);
            throw new IOException(exception.getMessage());
        }
    }

    private void saveConfig(CfgWriteContext context) throws ParsingException, IOException, IllegalAccessException
    {
        List<Field> cfgPropertyFields = CfgReflectionUtil.findAllCfgProperties(context.getInstance().getClass());
        for (final Field field : cfgPropertyFields)
        {
            context.setCurrentField(field);
            writeFieldInFile(context);
        }
    }

    private void writeFieldInFile(CfgWriteContext context) throws IllegalAccessException, IOException, ParsingException
    {
        Field field = context.getCurrentField();

        CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
        CfgParser<?> cfgParser = CfgFileHandler.PARSERS.get(cfgProperty.type());
        field.setAccessible(true);
        Object fieldValue = field.get(context.getInstance());
        field.setAccessible(false);

        String fieldValueAsString = fieldValue != null ? cfgParser.parseToString(context, fieldValue) : null;

        if (cfgProperty.skipIfNull() && fieldValueAsString == null)
            return;

        BufferedWriter bufferedWriter = context.getBufferedWriter();
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
