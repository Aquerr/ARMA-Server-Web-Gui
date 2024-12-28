package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.network.model.NetworkProperties;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.network.ServerNetworkService;
import pl.bartlomiejstepien.armaserverwebgui.web.request.NetworkPropertiesRequest;
import pl.bartlomiejstepien.armaserverwebgui.web.response.NetworkPropertiesResponse;
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
                .map(this::toResponseModel);
    }
    
    @PostMapping("/properties")
    public Mono<ResponseEntity<?>> saveServerSecurity(@RequestBody NetworkPropertiesRequest saveNetworkPropertiesRequest)
    {
        this.networkService.saveNetworkProperties(toDomainModel(saveNetworkPropertiesRequest));
        return Mono.just(ResponseEntity.ok().build());
    }

    private NetworkProperties toDomainModel(NetworkPropertiesRequest request)
    {
        return NetworkProperties.builder()
                .upnp(request.isUpnp())
                .maxPing(request.getMaxPing())
                .loopback(request.isLoopback())
                .disconnectTimeout(request.getDisconnectTimeout())
                .maxDesync(request.getMaxDesync())
                .maxPacketLoss(request.getMaxPacketLoss())
                .enablePlayerDiag(request.isEnablePlayerDiag())
                .steamProtocolMaxDataSize(request.getSteamProtocolMaxDataSize())
                .minBandwidth(request.getMinBandwidth())
                .maxBandwidth(request.getMaxBandwidth())
                .maxMsgSend(request.getMaxMsgSend())
                .maxSizeGuaranteed(request.getMaxSizeGuaranteed())
                .maxSizeNonGuaranteed(request.getMaxSizeNonGuaranteed())
                .minErrorToSend(request.getMinErrorToSend())
                .minErrorToSendNear(request.getMinErrorToSendNear())
                .maxCustomFileSize(request.getMaxCustomFileSize())
                .maxPacketSize(request.getMaxPacketSize())
                .kickTimeouts(request.getKickTimeouts())
                .build();
    }

    private NetworkPropertiesResponse toResponseModel(NetworkProperties networkProperties)
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
                .kickTimeouts(networkProperties.getKickTimeouts())
                .build();
    }
}
