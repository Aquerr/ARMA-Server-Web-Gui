package pl.bartlomiejstepien.armaserverwebgui.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/general")
@AllArgsConstructor
public class GeneralController
{
    private final ASWGConfig aswgConfig;

    @GetMapping("/server-directory")
    public Mono<ServerDirectoryResponse> getServerDirectory()
    {
        return Mono.just(aswgConfig.getServerDirectoryPath())
                .map(ServerDirectoryResponse::of);
    }

    @PostMapping("/server-directory")
    public Mono<ResponseEntity<Void>> updateServerDirectory(@RequestBody UpdateServerDirectoryRequest updateServerDirectoryRequest)
    {
        this.aswgConfig.setServerDirectoryPath(updateServerDirectoryRequest.getPath());
        return Mono.just(ResponseEntity.ok().build());
    }

    @Value(staticConstructor = "of")
    private static class ServerDirectoryResponse
    {
        String path;
    }

    @Data
    @RequiredArgsConstructor
    private static class UpdateServerDirectoryRequest
    {
        String path;
    }
}
