package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workshop")
@AllArgsConstructor
public class WorkshopRestController
{
    private final SteamService steamService;
    private final ModService modService;

    @PostMapping("/query")
    public Mono<ArmaWorkshopQueryResponse> queryWorkshop(@RequestBody WorkshopQueryRequest request)
    {
        return Mono.just(steamService.queryWorkshopMods(toWorkshopQueryParams(request)));
    }

    @GetMapping("/installed-items")
    public Mono<InstalledItemsResponse> getInstalledItems()
    {
        return modService.getInstalledWorkshopMods().collectList().map(this::toInstalledItemsResponse);
    }

    private InstalledItemsResponse toInstalledItemsResponse(List<ArmaWorkshopMod> armaWorkshopMods)
    {
        return new InstalledItemsResponse(armaWorkshopMods);
    }

    private WorkshopQueryParams toWorkshopQueryParams(WorkshopQueryRequest request)
    {
        return WorkshopQueryParams.builder()
                .cursor(request.getCursor())
                .searchText(request.getSearchText())
                .build();
    }

    @Value
    public static class InstalledItemsResponse {
        List<ArmaWorkshopMod> mods;
    }

    @Data
    public static class WorkshopQueryRequest
    {
        private String cursor;
        private String searchText;
    }
}
