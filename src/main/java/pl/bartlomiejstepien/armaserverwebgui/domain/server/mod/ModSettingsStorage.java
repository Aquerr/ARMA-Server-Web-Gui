package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ModSettingsStorage
{
    private static final String ACTIVE_MOD_SETTINGS_NAME = "cba_settings";
    private static final String ACTIVE_MOD_SETTINGS_FILE_NAME = ACTIVE_MOD_SETTINGS_NAME + ".sqf";
    private static final String DEFAULT_MOD_SETTINGS_NAME = "default";

    private final Lazy<Path> modSettingsDirPath;
    private final AswgFileNameNormalizer fileNameNormalizer;
    private final ModSettingsRepository modSettingsRepository;
    private final ASWGConfig aswgConfig;

    public ModSettingsStorage(ASWGConfig aswgConfig,
                              AswgFileNameNormalizer fileNameNormalizer,
                              ModSettingsRepository modSettingsRepository)
    {
        this.aswgConfig = aswgConfig;
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

    @Scheduled(fixedDelay = 20, timeUnit = TimeUnit.MINUTES)
    public void installModSettings()
    {
        log.info("Scanning for new mod/addon settings files...");
        if (!this.aswgConfig.isModSettingsInstallationScannerEnabled())
        {
            log.info("Mod/Addon settings scanner is disabled. Skipping...");
        }

        scanModSettingsForNewSettingsFiles();
    }

    public void deactivateModSettingsFile(String name)
    {
        String fileName = prepareFileName(name, false);

        try
        {
            Files.move(this.modSettingsDirPath.get().resolve(ACTIVE_MOD_SETTINGS_FILE_NAME), this.modSettingsDirPath.get().resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
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

    public ModSettingsEntity findByName(String name)
    {
        return this.modSettingsRepository.findByName(name).orElse(null);
    }

    public ModSettingsEntity findById(long id)
    {
        return this.modSettingsRepository.findById(id).orElse(null);
    }

    public void deactivateAll()
    {
        this.modSettingsRepository.disableAll();
    }

    public ModSettingsEntity save(ModSettingsEntity modSettingsEntity)
    {
        return this.modSettingsRepository.save(modSettingsEntity);
    }

    public List<ModSettingsEntity> findAll()
    {
        return this.modSettingsRepository.findAll();
    }

    public void delete(ModSettingsEntity modSettingsEntity)
    {
        this.modSettingsRepository.delete(modSettingsEntity);
    }

    public ModSettingsEntity findActive()
    {
        return this.modSettingsRepository.findFirstByActiveTrue().orElse(null);
    }

    private String prepareFileName(String name, boolean active)
    {
        if (active) {
            return ACTIVE_MOD_SETTINGS_FILE_NAME;
        } else {
            return fileNameNormalizer.normalize(name) + ".sqf";
        }
    }

    private String stripExtension(String fileName)
    {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private void scanModSettingsForNewSettingsFiles()
    {
        Path modSettingsDirPath = this.modSettingsDirPath.get();
        if (Files.notExists(modSettingsDirPath))
            return;

        List<ModSettingsEntity> existingModSettings = this.modSettingsRepository.findAll().stream()
                .toList();
        List<String> existingSettingsNames = existingModSettings.stream()
                .map(ModSettingsEntity::getName)
                .toList();


        List<String> settingsFileNames = Arrays.stream(modSettingsDirPath.toFile().list()).toList();

        Set<String> settingsToInstall = settingsFileNames.stream()
                .map(this::stripExtension)
                .filter(name -> !existingSettingsNames.contains(name))
                .collect(Collectors.toSet());

        if (settingsToInstall.contains(ACTIVE_MOD_SETTINGS_NAME)
                && existingModSettings.stream().anyMatch(ModSettingsEntity::isActive))
        {
            settingsToInstall.remove(ACTIVE_MOD_SETTINGS_NAME);
        }

        for (String settingsName : settingsToInstall)
        {
            ModSettingsEntity modSettingsEntity;

            if (settingsName.equals(ACTIVE_MOD_SETTINGS_NAME))
            {
                modSettingsEntity = ModSettingsEntity.builder()
                        .name(DEFAULT_MOD_SETTINGS_NAME)
                        .active(true)
                        .build();
            }
            else
            {
                modSettingsEntity = ModSettingsEntity.builder()
                        .name(settingsName)
                        .active(false)
                        .build();
            }

            this.modSettingsRepository.save(modSettingsEntity);
        }
    }
}
