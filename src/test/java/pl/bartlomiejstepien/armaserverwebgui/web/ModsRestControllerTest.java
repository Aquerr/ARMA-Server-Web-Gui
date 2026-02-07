package pl.bartlomiejstepien.armaserverwebgui.web;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Mod;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.ModsCollection;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.FileUtils;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

@AutoConfigureMockMvc
class ModsRestControllerTest extends BaseIntegrationTest
{
    @Autowired
    private ASWGConfig aswgConfig;
    @Autowired
    private InstalledModRepository installedModRepository;

    @BeforeEach
    void setUp()
    {
        cleanUpMods();
    }

    @Test
    void should_get_mods() throws IOException, JSONException
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

    @Test
    void should_manage_mod() throws IOException
    {
        // given
        prepareMods();

        ModsRestController.ManageModsRequest manageModsRequest = new ModsRestController.ManageModsRequest();
        manageModsRequest.setName("@TESTMOD5");

        // when
        var response = postAuthenticatedRequest("/api/v1/mods/manage", manageModsRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var getModsResponse = getAuthenticatedRequest("/api/v1/mods", ModsCollection.class);

        var enabledModView = getModsResponse.getBody().getDisabledMods().stream()
                        .filter(mod -> mod.getName().equals("@testmod5"))
                        .findFirst();

        assertThat(enabledModView).map(Mod::getStatus).get().isEqualTo(ModStatus.READY);

        cleanUpMods();
    }

    @Test
    void should_manage_mod_return_error_when_mod_id_zero() throws IOException
    {
        // given
        prepareMods();

        ModsRestController.ManageModsRequest manageModsRequest = new ModsRestController.ManageModsRequest();
        manageModsRequest.setName("@TESTMOD4");

        // when
        var response = postAuthenticatedRequest("/api/v1/mods/manage", manageModsRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

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
                .dependenciesIds(List.of())
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
                .dependenciesIds(List.of())
                .build());
    }

    private void prepareNotManagedMods(Path modsPath) throws IOException
    {
        Files.createDirectories(modsPath.resolve("testmod2"));
        Files.createDirectories(modsPath.resolve("@TESTMOD4").resolve("Addons"));
        Files.createDirectories(modsPath.resolve("@TESTMOD5").resolve("Addons"));
        Files.writeString(modsPath.resolve("@TESTMOD5").resolve("meta.cpp"), "publishedid=999;", StandardOpenOption.CREATE);
    }
}