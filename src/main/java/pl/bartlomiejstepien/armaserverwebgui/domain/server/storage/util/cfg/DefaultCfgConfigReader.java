package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg;

import lombok.extern.slf4j.Slf4j;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.parser.CfgClassParser;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
public class DefaultCfgConfigReader implements CfgConfigReader
{
    public static final DefaultCfgConfigReader INSTNACE = new DefaultCfgConfigReader();

    @Override
    public <T> T readConfig(File file, Class<T> clazz) throws IOException
    {
        try
        {
            if (!file.exists())
                return clazz.getDeclaredConstructor().newInstance();

            try(FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader))
            {
                return parse(new CfgReadContext(new StringBuilder(), bufferedReader, clazz), clazz);
            }
        }
        catch (Exception exception)
        {
            log.error("Error during parsing of cfg file.", exception);
            throw new IOException(exception.getMessage());
        }
    }

    private static <T> T parse(CfgReadContext context, Class<T> clazz) throws ParsingException
    {
        CfgClassParser cfgClassParser = (CfgClassParser) CfgFileHandler.PARSERS.get(PropertyType.CLASS);
        return cfgClassParser.parse(context, clazz);
    }
}
