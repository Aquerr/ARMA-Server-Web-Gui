package pl.bartlomiejstepien.armaserverwebgui.util.cfg;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.parser.*;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.type.PropertyType;

import java.io.*;
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
