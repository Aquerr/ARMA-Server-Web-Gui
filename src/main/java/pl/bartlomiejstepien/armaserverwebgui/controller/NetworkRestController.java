package pl.bartlomiejstepien.armaserverwebgui.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.model.NetworkProperties;
import pl.bartlomiejstepien.armaserverwebgui.service.ServerNetworkService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/network")
@AllArgsConstructor
public class NetworkRestController
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
                .maxPing(saveNetworkPropertiesRequest.getMaxPing())
                .build();
    }

    private GetNetworkPropertiesResponse toViewResponse(NetworkProperties networkProperties)
    {
        return GetNetworkPropertiesResponse.builder()
                .maxPing(networkProperties.getMaxPing())
                .build();
    }

    @Builder
    @Data
    private static class SaveNetworkPropertiesRequest
    {
        private int maxPing;
    }

    @Builder
    @Data
    private static class GetNetworkPropertiesResponse
    {
        private int maxPing;
    }
}
