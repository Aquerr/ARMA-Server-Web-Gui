package pl.bartlomiejstepien.armaserverwebgui.application.scheduling;

import lombok.extern.slf4j.Slf4j;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.model.JobExecutionStatus;

import java.time.OffsetDateTime;

@Slf4j
public abstract class AswgJob implements Runnable
{
    protected final JobExecutionInfoService jobExecutionInfoService;

    protected AswgJob(JobExecutionInfoService jobExecutionInfoService)
    {
        this.jobExecutionInfoService = jobExecutionInfoService;
    }

    public abstract String getName();

    protected abstract void runJob();

    @Override
    public void run()
    {
        try
        {
            this.jobExecutionInfoService.saveJobExecutionStart(getName());
            runJob();
            this.jobExecutionInfoService.saveJobExecutionFinish(getName(), OffsetDateTime.now(), JobExecutionStatus.SUCCESS,
                    JobExecutionStatus.SUCCESS.getCode());
        }
        catch (Exception exception)
        {
            log.error("Could not execute job '{}'", getName(), exception);
            this.jobExecutionInfoService.saveJobExecutionFinish(getName(), OffsetDateTime.now(), JobExecutionStatus.FAILURE, exception.getMessage());
        }
    }
}
