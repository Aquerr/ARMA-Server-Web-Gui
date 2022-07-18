package pl.bartlomiejstepien.armaserverwebgui.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.bartlomiejstepien.armaserverwebgui.IntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

@IntegrationTest
class GeneralControllerTest
{
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ASWGConfig aswgConfig;

    @Test
    @WithMockUser
    void getServerDirectoryShouldReturnServerDirectoryFromASWGConfig()
    {
        given(aswgConfig.getServerDirectoryPath()).willReturn("fake/fakeServerDirectory");

        webTestClient.get()
                .uri("/api/v1/general/server-directory")
                .exchange()
                .expectStatus()
                    .isOk()
                .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                    .json(loadJsonIntegrationContractFor("general/get-server-directory-response.json"));
    }

    @Test
    void getServerDirectoryShouldTriggerUnauthorizedErrorWhenUserNotAuthorized()
    {
        webTestClient.get()
                .uri("/api/v1/general/server-directory")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser
    void updateServerDirectoryShouldUpdateServerDirectoryInASWGConfig()
    {
        webTestClient.post()
                .uri("/api/v1/general/server-directory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loadJsonIntegrationContractFor("general/update-server-directory-request.json"))
                .exchange()
                .expectStatus()
                .isOk();

        verify(aswgConfig).setServerDirectoryPath("fake/fakeServerDirectory");
    }

    @Test
    void updateServerDirectoryShouldTriggerUnauthorizedErrorWhenUserNotAuthorized()
    {
        webTestClient.post()
                .uri("/api/v1/general/server-directory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loadJsonIntegrationContractFor("general/update-server-directory-request.json"))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }
}