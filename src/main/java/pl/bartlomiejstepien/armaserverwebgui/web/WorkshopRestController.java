package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/workshop")
@AllArgsConstructor
public class WorkshopRestController
{
    private final SteamService steamService;

    @GetMapping("/query")
    public Mono<ArmaWorkshopQueryResponse> queryWorkshop(WorkshopQueryRequest request)
    {
        return Mono.just(steamService.queryWorkshopMods(toWorkshopQueryParams(request)));
    }

    private WorkshopQueryParams toWorkshopQueryParams(WorkshopQueryRequest request)
    {
        return WorkshopQueryParams.builder()
                .cursor(request.getCursor())
                .searchText(request.getSearchText())
                .build();
    }

    @Data
    public static class WorkshopQueryRequest
    {
        private String cursor;
        private String searchText;
    }
}
