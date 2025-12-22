package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;

import static org.junit.jupiter.api.Assertions.*;

class ModsFilesRestControllerTest extends BaseIntegrationTest
{
    @Autowired
    private ModService modService;

    @BeforeEach
    void setUp()
    {
        modService.getInstalledMods().stream()
                .map(InstalledModEntity::getName)
                .forEach(modService::deleteMod);
    }

    @Test
    void shouldUploadModFile()
    {
        // given
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ClassPathResource("@testmod.zip"));
        parts.add("file", new ClassPathResource("@testmod2.zip"));

        // when
        var response = postAuthenticatedRequest("/api/v1/mods-files", parts, MediaType.MULTIPART_FORM_DATA_VALUE, Object.class);

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());

        modService.getInstalledMods().stream()
                .map(InstalledModEntity::getName)
                .forEach(modService::deleteMod);
    }
}