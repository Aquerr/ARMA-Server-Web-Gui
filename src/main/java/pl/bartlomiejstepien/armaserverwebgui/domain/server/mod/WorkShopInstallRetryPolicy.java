package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;

@Component
public class WorkShopInstallRetryPolicy
{
    /**
     * Allows for max 2 retries (when installAttemptCount starts with 1)
     *
     * @param request the request to check
     * @return true/false telling if the request can be retried
     */
    public boolean canRetry(WorkshopModInstallationRequest request)
    {
        return request.getInstallAttemptCount() <= 2;
    }
}
