package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.QueuedSteamTask;

@Component
public class SteamTaskRetryPolicy
{
    /**
     * Allows for max 2 retries (when retryCount starts with 1)
     *
     * @param task the task to check
     * @return true/false telling if the task can be retried
     */
    public boolean canRetry(QueuedSteamTask task)
    {
        return task.getRetryCount() < 2;
    }
}
