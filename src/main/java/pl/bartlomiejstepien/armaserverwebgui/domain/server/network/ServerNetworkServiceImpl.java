package pl.bartlomiejstepien.armaserverwebgui.domain.server.network;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.network.model.KickTimeoutType;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.network.model.NetworkProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.KickTimeout;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.NetworkConfig;

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
                .kickTimeouts(prepareKickTimeouts(armaServerConfig))
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
        armaServerConfig.setKickTimeouts(prepareKickTimeouts(networkProperties.getKickTimeouts()));

        serverConfigStorage.saveServerConfig(armaServerConfig);
        saveNetworkConfig(networkProperties);
    }

    private KickTimeout[] prepareKickTimeouts(NetworkProperties.KickTimeouts kickTimeouts)
    {
        return new KickTimeout[] {
                KickTimeout.builder()
                        .kickId(KickTimeoutType.MANUAL_KICK.getKickId())
                        .timeout(kickTimeouts.getManualKickTimeoutSeconds())
                        .build(),
                KickTimeout.builder()
                        .kickId(KickTimeoutType.CONNECTIVITY_KICK.getKickId())
                        .timeout(kickTimeouts.getConnectivityKickTimeoutSeconds())
                        .build(),
                KickTimeout.builder()
                        .kickId(KickTimeoutType.BATTL_EYE_KICK.getKickId())
                        .timeout(kickTimeouts.getBattlEyeKickTimeoutSeconds())
                        .build(),
                KickTimeout.builder()
                        .kickId(KickTimeoutType.HARMLESS_KICK.getKickId())
                        .timeout(kickTimeouts.getHarmlessKickTimeoutSeconds())
                        .build()
        };
    }

    private NetworkProperties.KickTimeouts prepareKickTimeouts(ArmaServerConfig armaServerConfig)
    {
        final Map<KickTimeoutType, Integer> kickTimeoutsMap = Arrays.stream(armaServerConfig.getKickTimeouts())
                .collect(Collectors.toMap(kickTimeout -> KickTimeoutType.findByKickId(kickTimeout.getKickId()), KickTimeout::getTimeout));

        return NetworkProperties.KickTimeouts.builder()
                .manualKickTimeoutSeconds(kickTimeoutsMap.get(KickTimeoutType.MANUAL_KICK))
                .connectivityKickTimeoutSeconds(kickTimeoutsMap.get(KickTimeoutType.CONNECTIVITY_KICK))
                .battlEyeKickTimeoutSeconds(kickTimeoutsMap.get(KickTimeoutType.BATTL_EYE_KICK))
                .harmlessKickTimeoutSeconds(kickTimeoutsMap.get(KickTimeoutType.HARMLESS_KICK))
                .build();
    }

    private void saveNetworkConfig(NetworkProperties networkProperties)
    {
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
        serverConfigStorage.saveNetworkConfig(networkConfig);
    }
}
