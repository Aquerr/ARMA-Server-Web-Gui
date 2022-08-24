package pl.bartlomiejstepien.armaserverwebgui.controller;


import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.service.ModService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mods")
@AllArgsConstructor

public class ModsRestController
{
    private final ModService modService;


    @GetMapping
    public Mono<GetInstalledModsResponse> getInstalledMods()
    {
        return Mono.just(this.modService.getInstalledModNames()).map(GetInstalledModsResponse::of);
    }

    @Value(staticConstructor = "of")
    private static class GetInstalledModsResponse
    {
        List<String> mods;
    }

}
