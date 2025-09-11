package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionCdlcUpdate;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionCdlcView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.CdlcService;
import pl.bartlomiejstepien.armaserverwebgui.web.model.CdlcApiModel;
import pl.bartlomiejstepien.armaserverwebgui.web.response.GetCdlcListResponse;

@RestController
@RequestMapping("/api/v1/cdlc")
@AllArgsConstructor
public class CdlcRestController
{
    private final CdlcService cdlcService;

    @HasPermissionCdlcView
    @GetMapping()
    public GetCdlcListResponse getCdlcs()
    {
        return new GetCdlcListResponse(this.cdlcService.findAll()
                .stream()
                .map(cdlc -> CdlcApiModel.builder()
                        .id(cdlc.getId())
                        .name(cdlc.getName())
                        .enabled(cdlc.isEnabled())
                        .fileExists(cdlc.isFileExists())
                        .build())
                .toList());
    }

    @HasPermissionCdlcUpdate
    @PostMapping("/{id}/toggle")
    public void toggleCdlc(@PathVariable("id") long id)
    {
        this.cdlcService.toggleCdlc(id);
    }
}
