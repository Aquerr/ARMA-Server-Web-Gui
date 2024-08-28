package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.network.model.NetworkProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.network.ServerNetworkService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/network")
@AllArgsConstructor
public class ServerNetworkRestController
{
    private final ServerNetworkService networkService;

    @GetMapping("/properties")
    public Mono<NetworkPropertiesResponse> getNetworkProperties()
    {
        return Mono.just(this.networkService.getNetworkProperties())
                .map(this::toViewResponse);
    }
    
    @PostMapping("/properties")
    public Mono<ResponseEntity<?>> saveServerSecurity(@RequestBody NetworkPropertiesRequest saveNetworkPropertiesRequest)
    {
        this.networkService.saveNetworkProperties(toDomainModel(saveNetworkPropertiesRequest));
        return Mono.just(ResponseEntity.ok().build());
    }

    private NetworkProperties toDomainModel(NetworkPropertiesRequest networkPropertiesRequest)
    {
        return NetworkProperties.builder()
                .upnp(networkPropertiesRequest.isUpnp())
                .maxPing(networkPropertiesRequest.getMaxPing())
                .loopback(networkPropertiesRequest.isLoopback())
                .disconnectTimeout(networkPropertiesRequest.getDisconnectTimeout())
                .maxDesync(networkPropertiesRequest.getMaxDesync())
                .maxPacketLoss(networkPropertiesRequest.getMaxPacketLoss())
                .enablePlayerDiag(networkPropertiesRequest.isEnablePlayerDiag())
                .steamProtocolMaxDataSize(networkPropertiesRequest.getSteamProtocolMaxDataSize())
                .minBandwidth(networkPropertiesRequest.getMinBandwidth())
                .maxBandwidth(networkPropertiesRequest.getMaxBandwidth())
                .maxMsgSend(networkPropertiesRequest.getMaxMsgSend())
                .maxSizeGuaranteed(networkPropertiesRequest.getMaxSizeGuaranteed())
                .maxSizeNonGuaranteed(networkPropertiesRequest.getMaxSizeNonGuaranteed())
                .minErrorToSend(networkPropertiesRequest.getMinErrorToSend())
                .minErrorToSendNear(networkPropertiesRequest.getMinErrorToSendNear())
                .maxCustomFileSize(networkPropertiesRequest.getMaxCustomFileSize())
                .maxPacketSize(networkPropertiesRequest.getMaxPacketSize())
                .build();
    }

    private NetworkPropertiesResponse toViewResponse(NetworkProperties networkProperties)
    {
        return NetworkPropertiesResponse.builder()
                .upnp(networkProperties.isUpnp())
                .maxPing(networkProperties.getMaxPing())
                .loopback(networkProperties.isLoopback())
                .disconnectTimeout(networkProperties.getDisconnectTimeout())
                .maxDesync(networkProperties.getMaxDesync())
                .maxPacketLoss(networkProperties.getMaxPacketLoss())
                .enablePlayerDiag(networkProperties.isEnablePlayerDiag())
                .steamProtocolMaxDataSize(networkProperties.getSteamProtocolMaxDataSize())
                .minBandwidth(networkProperties.getMinBandwidth())
                .maxBandwidth(networkProperties.getMaxBandwidth())
                .maxMsgSend(networkProperties.getMaxMsgSend())
                .maxSizeGuaranteed(networkProperties.getMaxSizeGuaranteed())
                .maxSizeNonGuaranteed(networkProperties.getMaxSizeNonGuaranteed())
                .minErrorToSend(networkProperties.getMinErrorToSend())
                .minErrorToSendNear(networkProperties.getMinErrorToSendNear())
                .maxCustomFileSize(networkProperties.getMaxCustomFileSize())
                .maxPacketSize(networkProperties.getMaxPacketSize())
                .build();
    }

    @Builder
    @Data
    private static class NetworkPropertiesRequest
    {
        private boolean upnp;
        private int maxPing;
        private boolean loopback;
        private int disconnectTimeout;
        private int maxDesync;
        private int maxPacketLoss;
        private boolean enablePlayerDiag;
        private int steamProtocolMaxDataSize;

        // Performance properties
        private long minBandwidth;
        private long maxBandwidth;
        private int maxMsgSend;
        private int maxSizeGuaranteed;
        private int maxSizeNonGuaranteed;
        private String minErrorToSend;
        private String minErrorToSendNear;
        private int maxCustomFileSize;
        private int maxPacketSize;
    }

    @Builder
    @Data
    private static class NetworkPropertiesResponse
    {
        private boolean upnp;
        private int maxPing;
        private boolean loopback;
        private int disconnectTimeout;
        private int maxDesync;
        private int maxPacketLoss;
        private boolean enablePlayerDiag;
        private int steamProtocolMaxDataSize;

        // Performance properties
        private long minBandwidth;
        private long maxBandwidth;
        private int maxMsgSend;
        private int maxSizeGuaranteed;
        private int maxSizeNonGuaranteed;
        private String minErrorToSend;
        private String minErrorToSendNear;
        private int maxCustomFileSize;
        private int maxPacketSize;
    }
}
