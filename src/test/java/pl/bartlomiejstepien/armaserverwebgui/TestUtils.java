package pl.bartlomiejstepien.armaserverwebgui;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.String.format;

public class TestUtils
{
    public static String loadJsonIntegrationContractFor(String contractJsonFileName)
    {
        try
        {
            return loadJsonFromClasspath(format("/integration/mocks/__files/%s", contractJsonFileName));
        }
        catch (IOException exception)
        {
            throw new RuntimeException("Couldn't load contract " + contractJsonFileName, exception);
        }
    }

    private static String loadJsonFromClasspath(String path) throws IOException
    {
        ClassPathResource classPathResource = new ClassPathResource(path);
        return Files.readString(Paths.get(classPathResource.getURI()));
    }
}
