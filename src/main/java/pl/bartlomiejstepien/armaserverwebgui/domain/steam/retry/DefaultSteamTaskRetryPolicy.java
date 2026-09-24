package pl.bartlomiejstepien.armaserverwebgui.domain.steam.retry;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.QueuedSteamTask;

@Component
public class DefaultSteamTaskRetryPolicy implements SteamTaskRetryPolicy
{
    private final SteamTaskRetryPolicy delegate = new SimpleSteamTaskRetryPolicy();

    @Override
    public boolean canRetry(QueuedSteamTask task)
    {
        return delegate.canRetry(task);
    }
}
