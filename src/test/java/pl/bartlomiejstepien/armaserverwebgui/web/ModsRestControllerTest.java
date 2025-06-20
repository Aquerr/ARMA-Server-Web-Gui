package pl.bartlomiejstepien.armaserverwebgui.web;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.FileUtils;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;

import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

@AutoConfigureMockMvc
class ModsRestControllerTest extends BaseIntegrationTest
{
    @Autowired
    private ASWGConfig aswgConfig;
    @Autowired
    private InstalledModRepository installedModRepository;

    @Test
    void shouldGetModsView() throws IOException, JSONException
    {
        // given
        prepareMods();

        // when
        var response = getAuthenticatedRequest("/api/v1/mods");

        // then
        JSONAssert.assertEquals(
                loadJsonIntegrationContractFor("mods/get-mods-response.json"),
                response.getBody(),
                JSONCompareMode.LENIENT);

        cleanUpMods();
    }

    private void cleanUpMods()
    {
        installedModRepository.deleteAll();
        Path modsPath = Paths.get(aswgConfig.getServerDirectoryPath()).resolve(aswgConfig.getModsDirectoryPath());
        FileUtils.deleteFilesRecursively(modsPath, false);
    }

    private void prepareMods() throws IOException
    {
        Path modsPath = Paths.get(aswgConfig.getServerDirectoryPath()).resolve(aswgConfig.getModsDirectoryPath());
        prepareEnabledMods(modsPath);
        prepareDisabledMods(modsPath);
        prepareNotManagedMods(modsPath);
    }

    private void prepareEnabledMods(Path modsPath) throws IOException
    {
        Files.createDirectories(modsPath.resolve("@testmod3").resolve("addons"));
        Files.writeString(modsPath.resolve("@testmod3").resolve("info.txt"), "THIS IS A LONG STRING JUST TO FILL UP THE FILE", StandardOpenOption.CREATE);
        Files.writeString(modsPath.resolve("@testmod3").resolve("meta.cpp"), "", StandardOpenOption.CREATE);

        installedModRepository.saveAndFlush(InstalledModEntity.builder()
                .serverMod(false)
                .name("testmod3")
                .enabled(true)
                .directoryPath("@testmod3")
                .workshopFileId(456L)
                .createdDate(OffsetDateTime.now())
                .build());
    }

    private void prepareDisabledMods(Path modsPath) throws IOException
    {
        Files.createDirectories(modsPath.resolve("@testmod1"));
        Files.writeString(modsPath.resolve("@testmod1").resolve("info.txt"), "THIS IS A LONG STRING JUST TO FILL UP THE FILE", StandardOpenOption.CREATE);

        installedModRepository.saveAndFlush(InstalledModEntity.builder()
                .serverMod(true)
                .name("testmod1")
                .directoryPath("@testmod1")
                .workshopFileId(123L)
                .createdDate(OffsetDateTime.now())
                .build());
    }

    private void prepareNotManagedMods(Path modsPath) throws IOException
    {
        Files.createDirectories(modsPath.resolve("testmod2"));
        Files.createDirectories(modsPath.resolve("@testmod4").resolve("Addons"));
    }
}