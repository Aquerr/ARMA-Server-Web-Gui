package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgIntegerParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgMissionParamsClassParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgMissionsClassParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgQuotedStringParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgRawStringParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.parser.CfgStringArrayParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.cfg.type.PropertyType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

@Component
public class CfgConfigWriter
{
    public static final Map<PropertyType, CfgParser<?, ?>> PARSERS = Map.of(
            PropertyType.QUOTED_STRING, new CfgQuotedStringParser(),
            PropertyType.RAW_STRING, new CfgRawStringParser(),
            PropertyType.INTEGER, new CfgIntegerParser(),
            PropertyType.STRING_ARRAY, new CfgStringArrayParser(),
            PropertyType.MISSIONS, new CfgMissionsClassParser(),
            PropertyType.PARAMS, new CfgMissionParamsClassParser()
    );

    public void saveConfig(File file, ArmaServerConfig armaServerConfig)
    {
        // Go through each field and save it to file
        try(FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter))
        {
            Field[] declaredFields = ArmaServerConfig.class.getDeclaredFields();
            for (final Field field : declaredFields)
            {
                writeFieldInFile(armaServerConfig, field, bufferedWriter);
            }
        }
        catch (IOException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void writeFieldInFile(ArmaServerConfig armaServerConfig, Field field, BufferedWriter bufferedWriter) throws IllegalAccessException, IOException
    {
        CfgProperty cfgProperty = field.getAnnotation(CfgProperty.class);
        field.setAccessible(true);
        Object fieldValue = field.get(armaServerConfig);
        field.setAccessible(false);
        String fieldValueAsString = PARSERS.get(cfgProperty.type()).parseToString(fieldValue);

        if (cfgProperty.isClass())
        {
            bufferedWriter.write(fieldValueAsString);
        }
        else
        {
            bufferedWriter.write(cfgProperty.name() + " = " + fieldValueAsString + "\n");
        }
    }
}
