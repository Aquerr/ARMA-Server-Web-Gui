package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionWorkshopInstall;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;
import pl.bartlomiejstepien.armaserverwebgui.web.request.WorkshopQueryRequest;
import pl.bartlomiejstepien.armaserverwebgui.web.response.ModDownloadQueueResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workshop")
@AllArgsConstructor
public class WorkshopRestController
{
    private final SteamService steamService;
    private final ModService modService;

    @GetMapping("/active")
    public WorkshopActiveResponse canUseWorkshop()
    {
        return new WorkshopActiveResponse(steamService.canUseWorkshop());
    }

    @HasPermissionWorkshopInstall
    @PostMapping("/query")
    public ArmaWorkshopQueryResponse queryWorkshop(@RequestBody WorkshopQueryRequest request)
    {
        return steamService.queryWorkshopMods(toWorkshopQueryParams(request));
    }

    @HasPermissionWorkshopInstall
    @GetMapping("/installed-items")
    public InstalledItemsResponse getInstalledItems()
    {
        return toInstalledItemsResponse(this.modService.getInstalledWorkshopMods(), this.modService.getWorkShopModInstallRequests());
    }

    @HasPermissionWorkshopInstall
    @GetMapping("/download-queue")
    public ModDownloadQueueResponse getDownloadQueue()
    {
        return toModDownloadQueueResponse(this.steamService.getInstallingMods());
    }

    @HasPermissionWorkshopInstall
    @PostMapping("/install")
    public WorkShopModInstallResponse installMod(@RequestBody WorkShopModInstallRequest request)
    {
        this.modService.installModFromWorkshop(request.getFileId(), request.getModName());
        return new WorkShopModInstallResponse(request.getFileId());
    }

    private static InstalledItemsResponse toInstalledItemsResponse(List<WorkshopMod> installedWorkshopMods,
                                                            List<WorkshopModInstallationRequest> installationRequests)
    {
        return new InstalledItemsResponse(installedWorkshopMods, toResponse(installationRequests));
    }

    private static List<WorkShopModInstallRequest> toResponse(List<WorkshopModInstallationRequest> requests)
    {
        return requests.stream().map(request ->
        {
            WorkShopModInstallRequest apiResponse = new WorkShopModInstallRequest();
            apiResponse.setFileId(request.getFileId());
            apiResponse.setModName(request.getTitle());
            return apiResponse;
        }).toList();
    }

    private static WorkshopQueryParams toWorkshopQueryParams(WorkshopQueryRequest request)
    {
        return WorkshopQueryParams.builder()
                .cursor(request.getCursor())
                .searchText(request.getSearchText())
                .build();
    }

    private static ModDownloadQueueResponse toModDownloadQueueResponse(List<WorkshopModInstallationRequest> installingMods)
    {
        return new ModDownloadQueueResponse(installingMods.stream()
                .map(installRequest ->
                        new ModDownloadQueueResponse.DownloadingMod(
                                installRequest.getFileId(),
                                installRequest.getTitle(),
                                installRequest.getInstallAttemptCount()))
                .toList()
        );
    }

    @Data
    public static class WorkShopModInstallRequest
    {
        private long fileId;
        private String modName;
    }

    public record WorkShopModInstallResponse(long fileId)
    {
    }

    public record InstalledItemsResponse(List<WorkshopMod> mods, List<WorkShopModInstallRequest> modsUnderInstallation)
    {
    }

    public record WorkshopActiveResponse(boolean active)
    {
    }
}
