package pl.bartlomiejstepien.armaserverwebgui.domain.steam.retry;

import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.QueuedSteamTask;

@FunctionalInterface
public interface SteamTaskRetryPolicy
{
    /**
     * Checks if the given task can be retried
     *
     * @param task the task to check
     * @return true/false telling if the task can be retried
     */
    boolean canRetry(QueuedSteamTask task);
}
