package pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfileEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.DifficultyConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.SystemUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.repository.DifficultyProfileRepository;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgValueHelper.toInt;

@Service
@Slf4j
public class DifficultyServiceImpl implements DifficultyService
{
    public static final String PROFILE_SUFFIX = ".Arma3Profile";
    private static final String DEFAULT_PROFILE_NAME = "Player";

    private final DifficultyProfileRepository difficultyProfileRepository;
    private final Lazy<Path> profilesDirectory;
    private final CfgFileHandler cfgFileHandler;

    public DifficultyServiceImpl(ASWGConfig aswgConfig,
                                 CfgFileHandler cfgFileHandler,
                                 DifficultyProfileRepository difficultyProfileRepository)
    {
        this.cfgFileHandler = cfgFileHandler;
        this.difficultyProfileRepository = difficultyProfileRepository;

        if (SystemUtils.isWindows())
        {
            this.profilesDirectory = Lazy.of(() -> Paths.get(aswgConfig.getServerDirectoryPath()).resolve("aswg_profiles"));
        }
        else
        {
            this.profilesDirectory = Lazy.of(() -> Paths.get(System.getProperty("user.home"))
                    .resolve(".local")
                    .resolve("share")
                    .resolve("Arma 3 - Other Profiles"));
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void postSetup()
    {
        if (Long.valueOf(0).equals(this.difficultyProfileRepository.count())
                && this.difficultyProfileRepository.findByName(DEFAULT_PROFILE_NAME).isEmpty())
        {
            log.info("Default profile {} not found. Creating one...", DEFAULT_PROFILE_NAME);
            DifficultyProfileEntity difficultyProfileEntity = new DifficultyProfileEntity(null, DEFAULT_PROFILE_NAME, true);
            this.difficultyProfileRepository.save(difficultyProfileEntity);
            saveToFile(map(new DifficultyConfig(), difficultyProfileEntity));
        }
    }

    @Override
    public Path getProfilesDirectory()
    {
        return profilesDirectory.get();
    }

    @Override
    public String getActiveDifficultyProfile()
    {
        return difficultyProfileRepository.findFirstByActiveTrue()
                .map(DifficultyProfileEntity::getName)
                .orElse(DEFAULT_PROFILE_NAME);
    }

    @Override
    public List<DifficultyProfile> getDifficultyProfiles()
    {
        return difficultyProfileRepository.findAll().stream()
                .map(this::mapToDifficultyProfile)
                .toList();
    }

    private DifficultyProfile mapToDifficultyProfile(DifficultyProfileEntity difficultyProfileEntity)
    {
        DifficultyConfig difficultyConfig = readDifficultyFile(difficultyProfileEntity.getName());
        return map(difficultyConfig, difficultyProfileEntity);
    }

    @Override
    @Transactional
    public DifficultyProfileEntity saveDifficultyProfile(DifficultyProfile difficultyProfile)
    {
        DifficultyProfileEntity entity = Optional.ofNullable(difficultyProfile.getId())
                .flatMap(difficultyProfileRepository::findById)
                .orElse(null);

        if (entity != null && !entity.getName().equals(difficultyProfile.getName()))
        {
            deleteFile(entity.getName());
        }

        // Deactivate the currently active profile if this one is set to active
        if (difficultyProfile.isActive())
        {
            DifficultyProfileEntity activeProfile = difficultyProfileRepository.findFirstByActiveTrue().orElse(null);
            if (activeProfile != null
                    && !activeProfile.getId().equals(difficultyProfile.getId()))
            {
                activeProfile.setActive(false);
                difficultyProfileRepository.save(activeProfile);
            }
        }

        entity = difficultyProfileRepository.findByName(difficultyProfile.getName()).orElse(null);
        if (entity == null)
        {
            entity = new DifficultyProfileEntity();
        }
        entity.setName(difficultyProfile.getName());
        entity.setActive(difficultyProfile.isActive());

        saveToFile(difficultyProfile);
        return difficultyProfileRepository.save(entity);
    }

    @Override
    public void deleteDifficultyProfile(int id)
    {
        difficultyProfileRepository.findById(id)
                .map(difficultyProfileEntity ->
                {
                    deleteFile(difficultyProfileEntity.getName());
                    return difficultyProfileEntity;
                })
                .ifPresent(difficultyProfileRepository::delete);
    }

    @Override
    public void deleteDifficultyProfile(String name)
    {
        difficultyProfileRepository.findByName(name)
                .map(difficultyProfileEntity ->
                {
                    deleteFile(name);
                    return difficultyProfileEntity;
                })
                .ifPresent(difficultyProfileRepository::delete);
    }

    @Override
    public void importDifficultyProfileFromFile(String profileName)
    {
        DifficultyConfig difficultyConfig = readDifficultyFile(profileName);
        DifficultyProfileEntity difficultyProfileEntity = new DifficultyProfileEntity(null, profileName, false);
        this.difficultyProfileRepository.save(difficultyProfileEntity);
        saveToFile(map(difficultyConfig, difficultyProfileEntity));
    }

    private void deleteFile(String difficultyName)
    {
        File profilesDir = profilesDirectory.get().toFile();
        File[] files = profilesDir.listFiles();
        if (files == null)
            return;

        for (final File file : files)
        {
            if (file.getName().equals(difficultyName))
            {
                FileSystemUtils.deleteRecursively(file);
                break;
            }
        }
    }

    private void saveToFile(DifficultyProfile difficultyProfile)
    {
        DifficultyConfig difficultyConfig = map(difficultyProfile);
        File file = resolveDifficultyPath(difficultyProfile.getName()).toFile();

        try
        {
            log.info("Saving difficulty profile {} to {}", difficultyProfile.getName(), file.getAbsolutePath());
            cfgFileHandler.saveConfig(file, difficultyConfig);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    private DifficultyConfig readDifficultyFile(String difficultyName)
    {
        File file = resolveDifficultyPath(difficultyName).toFile();
        try
        {
            return cfgFileHandler.readConfig(file, DifficultyConfig.class);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    private Path resolveDifficultyPath(String difficultyName)
    {
        return this.profilesDirectory.get()
                .resolve(difficultyName)
                .resolve(difficultyName + PROFILE_SUFFIX);
    }

    private DifficultyConfig map(DifficultyProfile difficultyProfile)
    {
        DifficultyConfig difficultyConfig = new DifficultyConfig();
        DifficultyConfig.DifficultyPresets.CustomDifficulty customDifficulty = difficultyConfig.getDifficultyPresets().getCustomDifficulty();
        customDifficulty.setAiLevelPreset(difficultyProfile.getOptions().getAiLevelPreset());

        DifficultyConfig.DifficultyPresets.CustomDifficulty.Options options = customDifficulty.getOptions();
        DifficultyProfile.Options newOptions = difficultyProfile.getOptions();
        options.setReducedDamage(toInt(newOptions.isReducedDamage()));
        options.setGroupIndicators(newOptions.getGroupIndicators());
        options.setFriendlyTags(newOptions.getFriendlyTags());
        options.setEnemyTags(newOptions.getEnemyTags());
        options.setDetectedMines(newOptions.getDetectedMines());
        options.setCommands(newOptions.getCommands());
        options.setWaypoints(newOptions.getWaypoints());
        options.setTacticalPing(newOptions.getTacticalPing());

        options.setWeaponInfo(newOptions.getWeaponInfo());
        options.setStanceIndicator(newOptions.getStanceIndicator());
        options.setStaminaBar(toInt(newOptions.isStaminaBar()));
        options.setWeaponCrosshair(toInt(newOptions.isWeaponCrosshair()));
        options.setVisionAid(toInt(newOptions.isVisionAid()));

        options.setThirdPersonView(newOptions.getThirdPersonView());
        options.setCameraShake(toInt(newOptions.isCameraShake()));

        options.setScoreTable(toInt(newOptions.isScoreTable()));
        options.setDeathMessages(toInt(newOptions.isDeathMessages()));
        options.setVonId(toInt(newOptions.isVonId()));

        options.setMapContentFriendly(toInt(newOptions.isMapContentFriendly()));
        options.setMapContentEnemy(toInt(newOptions.isMapContentEnemy()));
        options.setMapContentMines(toInt(newOptions.isMapContentMines()));

        options.setAutoReport(toInt(newOptions.isAutoReport()));
        options.setMultipleSaves(toInt(newOptions.isMultipleSaves()));

        return difficultyConfig;
    }

    private DifficultyProfile map(DifficultyConfig config, DifficultyProfileEntity entity)
    {
        DifficultyConfig.DifficultyPresets.CustomDifficulty customDifficulty = config.getDifficultyPresets().getCustomDifficulty();
        DifficultyConfig.DifficultyPresets.CustomDifficulty.Options options = customDifficulty.getOptions();

        return DifficultyProfile.builder()
                .id(entity.getId())
                .name(entity.getName())
                .active(entity.isActive())
                .options(DifficultyProfile.Options.builder()
                        .aiLevelPreset(customDifficulty.getAiLevelPreset())
                        .reducedDamage(options.getReducedDamage() == 1)
                        .groupIndicators(options.getGroupIndicators())
                        .friendlyTags(options.getFriendlyTags())
                        .enemyTags(options.getEnemyTags())
                        .detectedMines(options.getDetectedMines())
                        .commands(options.getCommands())
                        .waypoints(options.getWaypoints())

                        .weaponInfo(options.getWeaponInfo())
                        .stanceIndicator(options.getStanceIndicator())
                        .staminaBar(options.getStaminaBar() == 1)
                        .weaponCrosshair(options.getWeaponCrosshair() == 1)
                        .visionAid(options.getVisionAid() == 1)

                        .thirdPersonView(options.getThirdPersonView())
                        .cameraShake(options.getCameraShake() == 1)

                        .scoreTable(options.getScoreTable() == 1)
                        .deathMessages(options.getDeathMessages() == 1)
                        .vonId(options.getVonId() == 1)

                        .mapContentFriendly(options.getMapContentFriendly() == 1)
                        .mapContentEnemy(options.getMapContentEnemy() == 1)
                        .mapContentMines(options.getMapContentMines() == 1)
                        .tacticalPing(options.getTacticalPing())

                        .autoReport(options.getAutoReport() == 1)
                        .multipleSaves(options.getMultipleSaves() == 1)
                        .build())
                .build();
    }
}
