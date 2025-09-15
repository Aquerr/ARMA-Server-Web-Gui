package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.util.AswgFileNameNormalizer;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModSettingsEntity;
import pl.bartlomiejstepien.armaserverwebgui.repository.ModSettingsRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ModSettingsStorage
{
    public static final String ACTIVE_MOD_SETTINGS_NAME = "cba_settings";
    public static final String ACTIVE_MOD_SETTINGS_FILE_NAME = ACTIVE_MOD_SETTINGS_NAME + ".sqf";

    private final Lazy<Path> modSettingsDirPath;
    private final AswgFileNameNormalizer fileNameNormalizer;
    private final ModSettingsRepository modSettingsRepository;

    public ModSettingsStorage(ASWGConfig aswgConfig,
                              AswgFileNameNormalizer fileNameNormalizer,
                              ModSettingsRepository modSettingsRepository)
    {
        this.fileNameNormalizer = fileNameNormalizer;
        this.modSettingsRepository = modSettingsRepository;
        this.modSettingsDirPath = Lazy.of(() -> Paths.get(aswgConfig.getServerDirectoryPath())
                .resolve("userconfig"));
    }

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event)
    {
        try
        {
            Files.createDirectories(this.modSettingsDirPath.get());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Path getModSettingsDirPath()
    {
        return modSettingsDirPath.get();
    }

    public void deactivateModSettingsFile(String name)
    {
        String fileName = prepareFileName(name, false);

        try
        {
            Files.move(this.modSettingsDirPath.get().resolve(ACTIVE_MOD_SETTINGS_FILE_NAME),
                    this.modSettingsDirPath.get().resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String readModSettingsFileContent(String name, boolean active)
    {
        String fileName = prepareFileName(name, active);
        log.info("Reading contents of {}", fileName);

        Path settingsFilePath = this.modSettingsDirPath.get().resolve(fileName);
        if (!Files.exists(settingsFilePath))
            return "";

        try
        {
            return Files.readString(settingsFilePath);
        }
        catch (IOException exception)
        {
            log.error("Could not read mod settings file.", exception);
            return "";
        }
    }

    public void saveModSettingsFileContent(String name, boolean active, String content)
    {
        String fileName = prepareFileName(name, active);
        try
        {
            Files.createDirectories(this.modSettingsDirPath.get());
            File modSettingsDir = this.modSettingsDirPath.get().toFile();
            File fileToUpdate = Arrays.stream(modSettingsDir.listFiles())
                    .filter(settingsFile -> settingsFile.getName().equals(fileName))
                    .findFirst()
                    .orElse(new File(this.modSettingsDirPath.get().toString(), fileName));
            if (!fileToUpdate.exists())
            {
                fileToUpdate.createNewFile();
            }

            Files.writeString(this.modSettingsDirPath.get().resolve(fileName), content);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void deleteModSettingsFile(String name, boolean active)
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
    }

    @Transactional(readOnly = true)
    public ModSettingsEntity findByName(String name)
    {
        return this.modSettingsRepository.findByName(name).orElse(null);
    }

    @Transactional(readOnly = true)
    public ModSettingsEntity findById(long id)
    {
        return this.modSettingsRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deactivateAll()
    {
        this.modSettingsRepository.disableAll();
    }

    @Transactional
    public ModSettingsEntity save(ModSettingsEntity modSettingsEntity)
    {
        return this.modSettingsRepository.save(modSettingsEntity);
    }

    @Transactional(readOnly = true)
    public List<ModSettingsEntity> findAll()
    {
        return this.modSettingsRepository.findAll();
    }

    @Transactional
    public void delete(ModSettingsEntity modSettingsEntity)
    {
        this.modSettingsRepository.delete(modSettingsEntity);
    }

    @Transactional(readOnly = true)
    public ModSettingsEntity findActive()
    {
        return this.modSettingsRepository.findFirstByActiveTrue().orElse(null);
    }

    private String prepareFileName(String name, boolean active)
    {
        if (active)
        {
            return ACTIVE_MOD_SETTINGS_FILE_NAME;
        }
        else
        {
            return fileNameNormalizer.normalize(name) + ".sqf";
        }
    }
}
