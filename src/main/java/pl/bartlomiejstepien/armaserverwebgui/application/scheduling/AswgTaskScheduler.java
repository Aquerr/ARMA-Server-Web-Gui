package pl.bartlomiejstepien.armaserverwebgui.application.scheduling;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@AllArgsConstructor
public class AswgTaskScheduler
{
    private static final ConcurrentHashMap<String, ScheduledJob> SCHEDULED_TASKS = new ConcurrentHashMap<>();

    private final TaskScheduler taskScheduler;

    public void schedule(AswgJob job, String cron)
    {
        // There can be only one active job with a given name
        cancel(job.getName());

        log.info("Starting job '{}' with cron: '{}'", job.getName(), cron);
        Trigger trigger = new CronTrigger(cron);
        ScheduledFuture<?> scheduledFuture = this.taskScheduler.schedule(job, trigger);
        if (scheduledFuture == null)
        {
            log.warn("Task with name: {} won't be scheduled. Cron: {}", job.getName(), cron);
            return;
        }
        SCHEDULED_TASKS.put(job.getName(), new ScheduledJob(scheduledFuture, trigger) );
    }

    public void cancel(String taskName)
    {
        ScheduledJob scheduledJob = SCHEDULED_TASKS.get(taskName);
        if (scheduledJob != null)
        {
            scheduledJob.scheduledFuture().cancel(true);
            SCHEDULED_TASKS.remove(taskName);
        }
        else
        {
            log.info("Trying to cancel task with id: {} that is not scheduled", taskName);
        }
    }

    public Optional<Instant> getNextExecution(String name)
    {
        return Optional.ofNullable(SCHEDULED_TASKS.get(name))
                .map(ScheduledJob::trigger)
                .map(trigger -> trigger.nextExecution(new SimpleTriggerContext()));
    }

    private record ScheduledJob(ScheduledFuture<?> scheduledFuture, Trigger trigger)
    {

    }
}
