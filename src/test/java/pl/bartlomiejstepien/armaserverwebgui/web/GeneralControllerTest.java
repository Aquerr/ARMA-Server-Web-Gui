package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.bartlomiejstepien.armaserverwebgui.IntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.GeneralProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.general.GeneralService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

@IntegrationTest
class GeneralControllerTest
{
    private static final int MAX_PLAYERS = 10;
    private static final String API_GENERAL_PROPERTIES_URL = "/api/v1/general/properties";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GeneralService generalService;
    @MockBean
    private ASWGConfig aswgConfig;

    @Test
    @WithMockUser
    void getGeneralPropertiesShouldReturnServerDirectoryFromASWGConfig()
    {
        given(aswgConfig.getServerDirectoryPath()).willReturn("fake/fakeServerDirectory");
        given(generalService.getGeneralProperties()).willReturn(GeneralProperties.builder()
                .maxPlayers(MAX_PLAYERS)
                .build());

        webTestClient.get()
                .uri(API_GENERAL_PROPERTIES_URL)
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
    @WithMockUser
    void saveGeneralPropertiesShouldUpdateServerDirectoryInASWGConfig()
    {
        webTestClient.post()
                .uri(API_GENERAL_PROPERTIES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loadJsonIntegrationContractFor("general/save-general-properties-request.json"))
                .exchange()
                .expectStatus()
                .isOk();

        verify(aswgConfig).setServerDirectoryPath("fake/fakeServerDirectory");
        verify(generalService).saveGeneralProperties(GeneralProperties.builder()
                .maxPlayers(MAX_PLAYERS)
                .build());
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