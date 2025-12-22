package pl.bartlomiejstepien.armaserverwebgui.web;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;

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

        var response = getAuthenticatedRequest(SECURITY_PROPERTIES_URL);

        JSONAssert.assertEquals(loadJsonIntegrationContractFor("security/get-security-properties-response.json"), response.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    void shouldSaveSecurityProperties()
    {
        given(serverConfigStorage.getServerConfig()).willReturn(new ArmaServerConfig());

        var response = postAuthenticatedRequest(SECURITY_PROPERTIES_URL, loadJsonIntegrationContractFor("security/save-security-properties-request.json"));

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
        armaServerConfig.setVoteThreshold("0.3");
        armaServerConfig.setVoteMissionPlayers(2);
        return armaServerConfig;
    }
}