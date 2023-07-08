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
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workshop")
@AllArgsConstructor
public class WorkshopRestController
{
    private final SteamService steamService;
    private final ModService modService;

    @GetMapping("/active")
    public Mono<Boolean> canUseWorkshop()
    {
        return Mono.just(steamService.canUseWorkshop());
    }

    @PostMapping("/query")
    public Mono<ArmaWorkshopQueryResponse> queryWorkshop(@RequestBody WorkshopQueryRequest request)
    {
        return Mono.just(steamService.queryWorkshopMods(toWorkshopQueryParams(request)));
    }

    @GetMapping("/installed-items")
    public Mono<InstalledItemsResponse> getInstalledItems()
    {
        return Mono.zip(
                this.modService.getInstalledWorkshopMods().collectList(),
                Mono.just(this.modService.getWorkShopModInstallRequests())
        ).map(this::toInstalledItemsResponse);
    }

    private InstalledItemsResponse toInstalledItemsResponse(Tuple2<List<ArmaWorkshopMod>, List<WorkshopModInstallationRequest>> objects)
    {
        return new InstalledItemsResponse(objects.getT1(), toResponse(objects.getT2()));
    }

    private List<WorkShopModInstallRequest> toResponse(List<WorkshopModInstallationRequest> requests)
    {
        return requests.stream().map(request ->
        {
            WorkShopModInstallRequest apiResponse = new WorkShopModInstallRequest();
            apiResponse.setFileId(request.getFileId());
            apiResponse.setModName(request.getTitle());
            return apiResponse;
        }).toList();
    }

    @PostMapping("/install")
    public Mono<WorkShopModInstallResponse> installMod(@RequestBody WorkShopModInstallRequest request)
    {
        return this.modService.installModFromWorkshop(request.getFileId(), request.getModName())
                .thenReturn(new WorkShopModInstallResponse(request.getFileId()));
    }

    private WorkshopQueryParams toWorkshopQueryParams(WorkshopQueryRequest request)
    {
        return WorkshopQueryParams.builder()
                .cursor(request.getCursor())
                .searchText(request.getSearchText())
                .build();
    }

    @Data
    public static class WorkShopModInstallRequest
    {
        private long fileId;
        private String modName;
    }

    @Value
    public static class WorkShopModInstallResponse
    {
        long fileId;
    }

    @Value
    public static class InstalledItemsResponse
    {
        List<ArmaWorkshopMod> mods;
        List<WorkShopModInstallRequest> modsUnderInstallation;
    }

    @Data
    public static class WorkshopQueryRequest
    {
        private String cursor;
        private String searchText;
    }
}
