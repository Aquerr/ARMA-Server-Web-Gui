package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

class ServerSecurityRestControllerTest extends BaseIntegrationTest
{
    private static final String SECURITY_PROPERTIES_URL = "/api/v1/security";

    @MockitoBean
    private ServerConfigStorage serverConfigStorage;

    @Test
    void shouldReturnSecurityProperties()
    {
        given(serverConfigStorage.getServerConfig()).willReturn(prepareArmaServerConfig());

        webTestClient.get()
                .uri(SECURITY_PROPERTIES_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createUserAndJwt())
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(loadJsonIntegrationContractFor("security/get-security-properties-response.json"));
    }

    @Test
    void shouldSaveSecurityProperties()
    {
        given(serverConfigStorage.getServerConfig()).willReturn(new ArmaServerConfig());

        webTestClient.post()
                .uri(SECURITY_PROPERTIES_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createUserAndJwt())
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
        armaServerConfig.setFilePatchingExceptions(new String[]{"312312312312321"});
        armaServerConfig.setAllowedLoadFileExtensions(new String[]{"SQF"});
        armaServerConfig.setAdmins(new String[]{"12341235421321"});
        armaServerConfig.setAllowedVoteCmds(null);
        return armaServerConfig;
    }
}