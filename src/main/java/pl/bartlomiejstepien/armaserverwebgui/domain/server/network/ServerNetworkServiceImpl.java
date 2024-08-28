package pl.bartlomiejstepien.armaserverwebgui.domain.server.network;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.network.model.NetworkProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.NetworkConfig;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ServerNetworkServiceImpl implements ServerNetworkService
{
    private final ServerConfigStorage serverConfigStorage;

    @Override
    public NetworkProperties getNetworkProperties()
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        NetworkConfig networkConfig = serverConfigStorage.getNetworkConfig();
        return NetworkProperties.builder()
                .upnp(Boolean.parseBoolean(armaServerConfig.getUpnp()))
                .maxPing(armaServerConfig.getMaxPing())
                .loopback(Boolean.parseBoolean(armaServerConfig.getLoopback()))
                .disconnectTimeout(armaServerConfig.getDisconnectTimeout())
                .maxDesync(armaServerConfig.getMaxdesync())
                .maxPacketLoss(armaServerConfig.getMaxpacketloss())
                .enablePlayerDiag(armaServerConfig.getEnablePlayerDiag() == 1)
                .steamProtocolMaxDataSize(armaServerConfig.getSteamProtocolMaxDataSize())
                .minBandwidth(networkConfig.getMinBandwidth())
                .maxBandwidth(networkConfig.getMaxBandwidth())
                .maxMsgSend(networkConfig.getMaxMsgSend())
                .maxSizeGuaranteed(networkConfig.getMaxSizeGuaranteed())
                .maxSizeNonGuaranteed(networkConfig.getMaxSizeNonGuaranteed())
                .minErrorToSend(networkConfig.getMinErrorToSend())
                .minErrorToSendNear(networkConfig.getMinErrorToSendNear())
                .maxCustomFileSize(networkConfig.getMaxCustomFileSize())
                .maxPacketSize(Optional.ofNullable(networkConfig.getSockets())
                        .map(NetworkConfig.Sockets::getMaxPacketSize)
                        .orElseThrow())
                .build();
    }

    @Override
    public void saveNetworkProperties(NetworkProperties networkProperties)
    {
        ArmaServerConfig armaServerConfig = serverConfigStorage.getServerConfig();
        armaServerConfig.setMaxPing(networkProperties.getMaxPing());
        armaServerConfig.setUpnp(String.valueOf(networkProperties.isUpnp()));
        armaServerConfig.setLoopback(String.valueOf(networkProperties.isLoopback()));
        armaServerConfig.setDisconnectTimeout(networkProperties.getDisconnectTimeout());
        armaServerConfig.setMaxdesync(networkProperties.getMaxDesync());
        armaServerConfig.setMaxpacketloss(networkProperties.getMaxPacketLoss());
        armaServerConfig.setEnablePlayerDiag(networkProperties.isEnablePlayerDiag() ? 1 : 0);
        armaServerConfig.setSteamProtocolMaxDataSize(networkProperties.getSteamProtocolMaxDataSize());

        NetworkConfig networkConfig = serverConfigStorage.getNetworkConfig();
        networkConfig.setMinBandwidth(networkProperties.getMinBandwidth());
        networkConfig.setMaxBandwidth(networkProperties.getMaxBandwidth());
        networkConfig.setMaxMsgSend(networkProperties.getMaxMsgSend());
        networkConfig.setMaxSizeGuaranteed(networkProperties.getMaxSizeGuaranteed());
        networkConfig.setMaxSizeNonGuaranteed(networkProperties.getMaxSizeNonGuaranteed());
        networkConfig.setMinErrorToSend(networkProperties.getMinErrorToSend());
        networkConfig.setMinErrorToSendNear(networkProperties.getMinErrorToSendNear());
        networkConfig.setMaxCustomFileSize(networkProperties.getMaxCustomFileSize());
        networkConfig.setSockets(new NetworkConfig.Sockets(networkProperties.getMaxPacketSize()));

        serverConfigStorage.saveServerConfig(armaServerConfig);
        serverConfigStorage.saveNetworkConfig(networkConfig);
    }
}
