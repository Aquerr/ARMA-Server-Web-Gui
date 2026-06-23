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

    private DifficultyService difficultyService;

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