package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ModSettingsStorage
{
    private static final String ACTIVE_MOD_SETTINGS_FILE_NAME = "cba_settings.sqf";

    private final Lazy<Path> modSettingsDirPath;
    private final ModFolderNameHelper modFolderNameHelper;

    public ModSettingsStorage(ASWGConfig aswgConfig,
                              ModFolderNameHelper modFolderNameHelper)
    {
        this.modFolderNameHelper = modFolderNameHelper;
        this.modSettingsDirPath = Lazy.of(() -> Paths.get(aswgConfig.getServerDirectoryPath())
                .resolve("userconfig"));
    }

    public Mono<String> readModSettingsFileContent(String name, boolean active)
    {
        String fileName = prepareFileName(name, active);
        log.info("Reading contents of {}", fileName);
        return Flux.using(
                () -> Files.newBufferedReader(this.modSettingsDirPath.get().resolve(fileName), StandardCharsets.UTF_8),
                reader -> Flux.fromStream(reader.lines()),
                reader ->
                {
                    try
                    {
                        reader.close();
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                })
                .collectList()
                .map(Collection::stream)
                .map(stringStream -> stringStream.collect(Collectors.joining(" ")));

//        return Mono.fromCallable(() -> Files.readString(this.modSettingsDirPath.get().resolve(fileName)));
    }

    private String prepareFileName(String name, boolean active)
    {
        if (active) {
            return ACTIVE_MOD_SETTINGS_FILE_NAME;
        } else {
            return modFolderNameHelper.normalize(name) + ".sqf";
        }
    }

    public Mono<Void> saveModSettingsFileContent(String name, boolean active, String content)
    {
        String fileName = prepareFileName(name, active);
        try
        {
            Files.createDirectories(this.modSettingsDirPath.get());
            Files.writeString(this.modSettingsDirPath.get().resolve(fileName), content);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return Mono.empty();
    }

    public Mono<Void> deleteModSettingsFile(String name, boolean active)
    {
        String fileName = prepareFileName(name, active);
        File[] files = this.modSettingsDirPath.get().toFile().listFiles();

        if (files != null)
        {
            for (final File file : files)
            {
                if (file.getName().equals(fileName))
                {
                    file.delete();
                    break;
                }
            }
        }

        return Mono.empty();
    }
}
