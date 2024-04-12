package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import lombok.AllArgsConstructor;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgArrayClassFieldValuesParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgIntegerParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgLongParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgMissionParamsClassParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgMissionsClassParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgQuotedStringParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgRawStringParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgStringArrayParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@AllArgsConstructor
public class CfgFileHandler
{
    public static final Map<PropertyType, CfgParser<?, ?>> PARSERS = Map.of(
            PropertyType.QUOTED_STRING, new CfgQuotedStringParser(),
            PropertyType.RAW_STRING, new CfgRawStringParser(),
            PropertyType.INTEGER, new CfgIntegerParser(),
            PropertyType.LONG, new CfgLongParser(),
            PropertyType.ARRAY_OF_STRINGS, new CfgStringArrayParser(),
            PropertyType.MISSIONS, new CfgMissionsClassParser(),
            PropertyType.PARAMS, new CfgMissionParamsClassParser(),
            PropertyType.ARRAY_OF_NO_FIELDS_OBJECT, new CfgArrayClassFieldValuesParser<>()
    );

    private final CfgConfigReader cfgConfigReader;
    private final CfgConfigWriter cfgConfigWriter;


    public <T> T readConfig(File file, Class<T> clazz) throws IOException
    {
        return cfgConfigReader.readConfig(file, clazz);
    }

    public <T> void saveConfig(File file, T instance) throws IOException
    {
        cfgConfigWriter.saveConfig(file, instance);
    }
}
