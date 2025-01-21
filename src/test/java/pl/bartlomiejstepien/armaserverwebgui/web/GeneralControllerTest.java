package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.GeneralService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

class GeneralControllerTest extends BaseIntegrationTest
{
    private static final int MAX_PLAYERS = 10;
    private static final String API_GENERAL_PROPERTIES_URL = "/api/v1/general/properties";

    @MockitoBean
    private GeneralService generalService;

    @Autowired
    private ASWGConfig aswgConfig;

    @Test
    void getGeneralPropertiesShouldReturnServerDirectoryFromASWGConfig()
    {
        given(generalService.getGeneralProperties()).willReturn(GeneralProperties.builder()
                .maxPlayers(MAX_PLAYERS)
                .build());

        webTestClient.get()
                .uri(API_GENERAL_PROPERTIES_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .exchange()
                .expectStatus()
                    .isOk()
                .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .json(loadJsonIntegrationContractFor("general/get-general-properties-response.json"));
    }

    @Test
    void getGeneralPropertiesShouldTriggerUnauthorizedErrorWhenUserNotAuthorized()
    {
        webTestClient.get()
                .uri(API_GENERAL_PROPERTIES_URL)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void saveGeneralPropertiesShouldUpdateServerAndModsDirectoryInASWGConfig()
    {
        String initialServerDirectory = aswgConfig.getServerDirectoryPath();
        String initialModsDirectory = aswgConfig.getModsDirectoryPath();

        webTestClient.post()
                .uri(API_GENERAL_PROPERTIES_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loadJsonIntegrationContractFor("general/save-general-properties-request.json"))
                .exchange()
                .expectStatus()
                .isOk();

        assertThat(aswgConfig.getServerDirectoryPath()).isEqualTo("fake/fakeServerDirectory");
        assertThat(aswgConfig.getModsDirectoryPath()).isEqualTo("anotherModsDirectory");
        verify(generalService).saveGeneralProperties(GeneralProperties.builder()
                .maxPlayers(MAX_PLAYERS)
                .build());

        aswgConfig.setServerDirectoryPath(initialServerDirectory);
        aswgConfig.setModsDirectoryPath(initialModsDirectory);
        aswgConfig.saveToFile();
    }

    @Test
    void saveGeneralPropertiesShouldTriggerUnauthorizedErrorWhenUserNotAuthorized()
    {
        webTestClient.post()
                .uri(API_GENERAL_PROPERTIES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loadJsonIntegrationContractFor("general/save-general-properties-request.json"))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }
}