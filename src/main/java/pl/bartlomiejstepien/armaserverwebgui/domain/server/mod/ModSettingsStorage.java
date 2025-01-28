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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        scanModSettingsForNewSettingsFiles()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    public Mono<Void> deactivateModSettingsFile(String name)
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
        return Mono.empty();
    }

    public Mono<String> readModSettingsFileContent(String name, boolean active)
    {
        String fileName = prepareFileName(name, active);
        log.info("Reading contents of {}", fileName);
        return Mono.just(this.modSettingsDirPath.get().resolve(fileName))
                .filter(Files::exists)
                .flatMap(filePath -> Mono.fromCallable(() -> Files.readString(filePath)))
                .switchIfEmpty(Mono.just(""));
    }

    public Mono<Void> saveModSettingsFileContent(String name, boolean active, String content)
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

    public Mono<ModSettingsEntity> findByName(String name)
    {
        return this.modSettingsRepository.findByName(name);
    }

    public Mono<ModSettingsEntity> findById(long id)
    {
        return this.modSettingsRepository.findById(id);
    }

    public Mono<Void> deactivateAll()
    {
        return this.modSettingsRepository.disableAll();
    }

    public Mono<ModSettingsEntity> save(ModSettingsEntity modSettingsEntity)
    {
        return this.modSettingsRepository.save(modSettingsEntity);
    }

    public Flux<ModSettingsEntity> findAll()
    {
        return this.modSettingsRepository.findAll();
    }

    public Mono<Void> delete(ModSettingsEntity modSettingsEntity)
    {
        return this.modSettingsRepository.delete(modSettingsEntity);
    }

    public Mono<ModSettingsEntity> findActive()
    {
        return this.modSettingsRepository.findFirstByActiveTrue();
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

    private Mono<Void> scanModSettingsForNewSettingsFiles()
    {
        return Mono.just(this.modSettingsDirPath.get())
                .filter(Files::exists)
                .mapNotNull(path -> path.toFile().list())
                .filter(Objects::nonNull)
                .mapNotNull(fileNames -> Stream.of(fileNames).toList())
                .zipWith(modSettingsRepository.findAll().collectList(), (settingsFileNames, settingsEntities) -> {
                    List<String> existingSettingsNames = settingsEntities.stream()
                            .map(ModSettingsEntity::getName)
                            .toList();

                    Set<String> settingsToInstall = settingsFileNames.stream()
                            .map(this::stripExtension)
                            .filter(name -> !existingSettingsNames.contains(name))
                            .collect(Collectors.toSet());

                    if (settingsToInstall.contains(ACTIVE_MOD_SETTINGS_NAME)
                            && settingsEntities.stream().anyMatch(ModSettingsEntity::isActive))
                    {
                        settingsToInstall.remove(ACTIVE_MOD_SETTINGS_NAME);
                    }
                    return settingsToInstall;
                })
                .flatMapMany(Flux::fromIterable)
                .map(settingsName -> {
                    if (settingsName.equals(ACTIVE_MOD_SETTINGS_NAME))
                    {
                        return ModSettingsEntity.builder()
                                .name(DEFAULT_MOD_SETTINGS_NAME)
                                .active(true)
                                .build();
                    }

                    return ModSettingsEntity.builder()
                            .name(settingsName)
                            .active(false)
                            .build();
                })
                .flatMap(this.modSettingsRepository::save)
                .then();
    }
}
