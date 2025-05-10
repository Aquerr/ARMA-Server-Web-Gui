package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionUnsafeOverwriteStartupParams;

@ConditionalOnProperty(
        value = "aswg.server.unsafe.startup-params.overwrite.web-edit.enabled",
        havingValue = "true"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/unsafe")
public class UnsafeController
{
    private final ASWGConfig aswgConfig;

    @HasPermissionUnsafeOverwriteStartupParams
    @PostMapping("/startup-params")
    public void overwriteStartupParams(@RequestBody StartupParamsOverwriteRequest request)
    {
        this.aswgConfig.getUnsafeProperties().setOverwriteStartupParamsValue(request.getParams());
        this.aswgConfig.saveToFile();
    }

    @Data
    public static class StartupParamsOverwriteRequest
    {
        private String params;
    }
}
