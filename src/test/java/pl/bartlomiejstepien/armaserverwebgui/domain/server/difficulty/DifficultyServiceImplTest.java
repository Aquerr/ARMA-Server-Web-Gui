package pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.util.Lazy;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.exception.CouldNotReadDifficultyProfileException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.repository.DifficultyProfileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DifficultyServiceImplTest
{
    @Autowired
    private DifficultyProfileRepository difficultyProfileRepository;

    @Autowired
    private CfgFileHandler cfgFileHandler;

    private DifficultyServiceImpl difficultyService;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException
    {
        Path difficultyProfilesPath = tempDir.resolve("difficulty_profiles");

        this.difficultyService = new DifficultyServiceImpl(
                cfgFileHandler,
                difficultyProfileRepository,
                Lazy.of(() -> difficultyProfilesPath)
        );
        setUpDifficultyProfilesFiles(difficultyProfilesPath);
    }

    @Test
    void shouldReadDifficultyProfileSuccessfully()
    {
        // when
        // then
        this.difficultyService.importDifficultyProfileFromFile("correct");
        DifficultyProfile difficultyProfile = this.difficultyService.getDifficultyProfile("correct");

        assertThat(difficultyProfile).isNotNull();
        assertThat(difficultyProfile.getName()).isEqualTo("correct");
        assertThat(difficultyProfile.getOptions()).satisfies(options -> {
            assertThat(options.isReducedDamage()).isFalse();
            assertThat(options.getGroupIndicators()).isEqualTo(1);
            assertThat(options.getEnemyTags()).isEqualTo(1);
            assertThat(options.getCommands()).isEqualTo(1);
            assertThat(options.getDetectedMines()).isEqualTo(1);
            assertThat(options.getWaypoints()).isEqualTo(1);
            assertThat(options.getTacticalPing()).isEqualTo(1);
            assertThat(options.getWeaponInfo()).isEqualTo(1);
            assertThat(options.getStanceIndicator()).isEqualTo(1);
            assertThat(options.isStaminaBar()).isTrue();
            assertThat(options.isWeaponCrosshair()).isTrue();
            assertThat(options.isVisionAid()).isTrue();
            assertThat(options.getThirdPersonView()).isEqualTo(1);
            assertThat(options.isCameraShake()).isTrue();
            assertThat(options.isScoreTable()).isTrue();
            assertThat(options.isDeathMessages()).isTrue();
            assertThat(options.isVonId()).isTrue();
            assertThat(options.isMapContentFriendly()).isTrue();
            assertThat(options.isMapContentEnemy()).isTrue();
            assertThat(options.isMapContentMines()).isTrue();
            assertThat(options.isAutoReport()).isTrue();
            assertThat(options.isMultipleSaves()).isTrue();
        });
        assertThat(difficultyProfile.getOptions().getAiLevelPreset()).isEqualTo(3);
        assertThat(difficultyProfile.getOptions().getSkillAI()).isEqualTo("0.2");
        assertThat(difficultyProfile.getOptions().getPrecisionAI()).isEqualTo("0.3");
    }

    @Test
    void shouldReadDifficultyProfileSuccessfullyWhenUnformatted()
    {
        // given
        // when
        // then
        this.difficultyService.importDifficultyProfileFromFile("unformatted");
        DifficultyProfile difficultyProfile = this.difficultyService.getDifficultyProfile("unformatted");
        assertThat(difficultyProfile).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenDifficultyProfileIsMissingASemicolon()
    {
        // given
        // when
        // then
        assertThrows(CouldNotReadDifficultyProfileException.class, () -> this.difficultyService.importDifficultyProfileFromFile("missing-semicolon"));
    }

    @Test
    void shouldSaveDifficultyProfileSuccessfully()
    {
        // given
        DifficultyProfile difficultyProfile = DifficultyProfile.builder()
                .name("new-profile")
                .options(DifficultyProfile.Options.builder()
                        .reducedDamage(true)
                        .skillAI("0.1")
                        .cameraShake(false)
                        .autoReport(true)
                        .commands(1)
                        .enemyTags(0)
                        .build())
                .build();

        // when
        difficultyService.saveDifficultyProfile(difficultyProfile);

        // then
        DifficultyProfile saved =  this.difficultyService.getDifficultyProfile("new-profile");
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("new-profile");
        assertThat(saved.getOptions()).satisfies(options -> {
            assertThat(options.isReducedDamage()).isTrue();
            assertThat(options.getSkillAI()).isEqualTo("0.1");
            assertThat(options.isCameraShake()).isFalse();
            assertThat(options.isAutoReport()).isTrue();
            assertThat(options.getCommands()).isEqualTo(1);
            assertThat(options.getEnemyTags()).isEqualTo(0);
        });
    }

    private static void setUpDifficultyProfilesFiles(Path difficultyProfileDir) throws IOException
    {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:arma-profiles/*");

        for (Resource resource : resources) {
            Path profileDir = difficultyProfileDir.resolve(StringUtils
                    .stripFilenameExtension(resource.getFilename()));
            Files.createDirectories(profileDir);
            Files.copy(resource.getInputStream(), profileDir.resolve(resource.getFilename()), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}