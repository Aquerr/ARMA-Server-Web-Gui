package pl.bartlomiejstepien.armaserverwebgui.web;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.MultiValueMap;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

class ServerSecurityRestControllerTest extends BaseIntegrationTest
{
    private static final String SECURITY_PROPERTIES_URL = "/api/v1/security";

    @MockitoBean
    private ServerConfigStorage serverConfigStorage;

    @Test
    void shouldReturnSecurityProperties() throws JSONException
    {
        given(serverConfigStorage.getServerConfig()).willReturn(prepareArmaServerConfig());

        var response = testRestTemplate.exchange(
                SECURITY_PROPERTIES_URL,
                HttpMethod.GET,
                new HttpEntity<>(null, MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                String.class
        );

        JSONAssert.assertEquals(loadJsonIntegrationContractFor("security/get-security-properties-response.json"), response.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    void shouldSaveSecurityProperties()
    {
        given(serverConfigStorage.getServerConfig()).willReturn(new ArmaServerConfig());

        var response = testRestTemplate.exchange(
                SECURITY_PROPERTIES_URL,
                HttpMethod.POST,
                new HttpEntity<>(loadJsonIntegrationContractFor("security/save-security-properties-request.json"), MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser(),
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                ))),
                String.class
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());

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
        armaServerConfig.setKickDuplicate(1);
        return armaServerConfig;
    }
}