package pl.bartlomiejstepien.armaserverwebgui.domain.steam.retry;

import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.QueuedSteamTask;

public class SimpleSteamTaskRetryPolicy implements SteamTaskRetryPolicy
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
