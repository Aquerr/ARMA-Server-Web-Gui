package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.bartlomiejstepien.armaserverwebgui.IntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

@IntegrationTest
class ServerSecurityRestControllerTest
{
    private static final String SECURITY_PROPERTIES_URL = "/api/v1/security";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ServerConfigStorage serverConfigStorage;

    @Test
    @WithMockUser
    void shouldReturnSecurityProperties()
    {
        given(serverConfigStorage.getServerConfig()).willReturn(prepareArmaServerConfig());

        webTestClient.get()
                .uri(SECURITY_PROPERTIES_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(loadJsonIntegrationContractFor("security/get-security-properties-response.json"));
    }

    @Test
    @WithMockUser
    void shouldSaveSecurityProperties()
    {
        given(serverConfigStorage.getServerConfig()).willReturn(new ArmaServerConfig());

        webTestClient.post()
                .uri(SECURITY_PROPERTIES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loadJsonIntegrationContractFor("security/save-security-properties-request.json"))
                .exchange()
                .expectStatus()
                .isOk();

        ArmaServerConfig expected = prepareArmaServerConfig();
        verify(serverConfigStorage).saveServerConfig(expected);
    }

    private ArmaServerConfig prepareArmaServerConfig()
    {
        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        armaServerConfig.setPassword("server_password");
        armaServerConfig.setPasswordAdmin("admin_password");
        armaServerConfig.setServerCommandPassword("command_password");
        armaServerConfig.setBattleEye(1);
        armaServerConfig.setVerifySignatures(2);
        armaServerConfig.setAllowedFilePatching(1);
        armaServerConfig.setAllowedLoadFileExtensions(new String[]{"SQF"});
        armaServerConfig.setAdmins(new String[]{"12341235421321"});
        return armaServerConfig;
    }
}