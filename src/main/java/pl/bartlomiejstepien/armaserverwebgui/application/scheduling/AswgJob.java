package pl.bartlomiejstepien.armaserverwebgui.application.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
public abstract class AswgJob implements Runnable
{
    protected final JobExecutionInfoService jobExecutionInfoService;

    protected abstract String getName();

    protected abstract void runJob();

    @Override
    public final void run()
    {
        try
        {
            runJob();
            this.jobExecutionInfoService.saveJobLastExecutionDate(getName(), OffsetDateTime.now());
        }
        catch (Exception exception)
        {
            log.error("Could not execute job '{}'", getName(), exception);
        }
    }
}
