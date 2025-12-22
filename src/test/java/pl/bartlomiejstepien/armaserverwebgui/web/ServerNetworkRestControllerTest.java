package pl.bartlomiejstepien.armaserverwebgui.web;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.NetworkConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.bartlomiejstepien.armaserverwebgui.TestUtils.loadJsonIntegrationContractFor;

class ServerNetworkRestControllerTest extends BaseIntegrationTest
{
    private static final String NETWORK_PROPERTIES_URL = "/api/v1/network/properties";

    @Autowired
    private ServerConfigStorage serverConfigStorage;

    @Test
    void shouldSaveNetworkProperties() throws JSONException
    {
        serverConfigStorage.saveServerConfig(prepareArmaServerConfig());
        serverConfigStorage.saveNetworkConfig(prepareNetworkConfig());

        var response1 = postAuthenticatedRequest(NETWORK_PROPERTIES_URL, loadJsonIntegrationContractFor("network/save-network-properties-request.json"));

        assertTrue(response1.getStatusCode().is2xxSuccessful());

        var response2 = getAuthenticatedRequest(NETWORK_PROPERTIES_URL);

        assertTrue(response2.getStatusCode().is2xxSuccessful());
        JSONAssert.assertEquals(loadJsonIntegrationContractFor("network/get-network-properties-response.json"), response2.getBody(), JSONCompareMode.LENIENT);
    }

    private ArmaServerConfig prepareArmaServerConfig()
    {
        ArmaServerConfig armaServerConfig = new ArmaServerConfig();
        armaServerConfig.setMaxPing(100);
        armaServerConfig.setUpnp("true");
        armaServerConfig.setLoopback("true");
        armaServerConfig.setDisconnectTimeout(30);
        armaServerConfig.setMaxdesync(10);
        armaServerConfig.setMaxpacketloss(150);
        armaServerConfig.setEnablePlayerDiag(1);
        armaServerConfig.setSteamProtocolMaxDataSize(300);

        return armaServerConfig;
    }

    private NetworkConfig prepareNetworkConfig()
    {
        NetworkConfig networkConfig = serverConfigStorage.getNetworkConfig();
        networkConfig.setMinBandwidth(100);
        networkConfig.setMaxBandwidth(120);
        networkConfig.setMaxMsgSend(140);
        networkConfig.setMaxSizeGuaranteed(160);
        networkConfig.setMaxSizeNonGuaranteed(180);
        networkConfig.setMinErrorToSend("0.67");
        networkConfig.setMinErrorToSendNear("0.123");
        networkConfig.setMaxCustomFileSize(1000);
        networkConfig.setSockets(new NetworkConfig.Sockets(13000));
        return networkConfig;
    }
}