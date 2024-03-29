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
    public Mono<GetNetworkPropertiesResponse> getNetworkProperties()
    {
        return Mono.just(this.networkService.getNetworkProperties())
                .map(this::toViewResponse);
    }
    
    @PostMapping("/properties")
    public Mono<ResponseEntity<?>> saveServerSecurity(@RequestBody SaveNetworkPropertiesRequest saveNetworkPropertiesRequest)
    {
        this.networkService.saveNetworkProperties(toDomainModel(saveNetworkPropertiesRequest));
        return Mono.just(ResponseEntity.ok().build());
    }

    private NetworkProperties toDomainModel(SaveNetworkPropertiesRequest saveNetworkPropertiesRequest)
    {
        return NetworkProperties.builder()
                .upnp(saveNetworkPropertiesRequest.isUpnp())
                .maxPing(saveNetworkPropertiesRequest.getMaxPing())
                .loopback(saveNetworkPropertiesRequest.isLoopback())
                .disconnectTimeout(saveNetworkPropertiesRequest.getDisconnectTimeout())
                .maxDesync(saveNetworkPropertiesRequest.getMaxDesync())
                .maxPacketLoss(saveNetworkPropertiesRequest.getMaxPacketLoss())
                .enablePlayerDiag(saveNetworkPropertiesRequest.isEnablePlayerDiag())
                .steamProtocolMaxDataSize(saveNetworkPropertiesRequest.getSteamProtocolMaxDataSize())
                .build();
    }

    private GetNetworkPropertiesResponse toViewResponse(NetworkProperties networkProperties)
    {
        return GetNetworkPropertiesResponse.builder()
                .upnp(networkProperties.isUpnp())
                .maxPing(networkProperties.getMaxPing())
                .loopback(networkProperties.isLoopback())
                .disconnectTimeout(networkProperties.getDisconnectTimeout())
                .maxDesync(networkProperties.getMaxDesync())
                .maxPacketLoss(networkProperties.getMaxPacketLoss())
                .enablePlayerDiag(networkProperties.isEnablePlayerDiag())
                .steamProtocolMaxDataSize(networkProperties.getSteamProtocolMaxDataSize())
                .build();
    }

    @Builder
    @Data
    private static class SaveNetworkPropertiesRequest
    {
        private boolean upnp;
        private int maxPing;
        private boolean loopback;
        private int disconnectTimeout;
        private int maxDesync;
        private int maxPacketLoss;
        private boolean enablePlayerDiag;
        private int steamProtocolMaxDataSize;
    }

    @Builder
    @Data
    private static class GetNetworkPropertiesResponse
    {
        private boolean upnp;
        private int maxPing;
        private boolean loopback;
        private int disconnectTimeout;
        private int maxDesync;
        private int maxPacketLoss;
        private boolean enablePlayerDiag;
        private int steamProtocolMaxDataSize;
    }
}
