package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.dto.JobExecution;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.model.JobExecutionStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionJobsSettingsUpdate;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.JobService;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.model.JobSettings;
import pl.bartlomiejstepien.armaserverwebgui.web.request.JobSettingsUpdate;
import pl.bartlomiejstepien.armaserverwebgui.web.response.JobSettingsResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/settings/jobs")
@RequiredArgsConstructor
public class JobsSettingsRestController
{
    private final JobService jobService;

    @GetMapping()
    public List<String> getJobsNames(
            @RequestParam(value = "names-only", required = false, defaultValue = "true")
            boolean namesOnly)
    {
        // For now, ignore namesOnly param.
        return jobService.getJobsNames().stream().toList();
    }

    @GetMapping("/{name}")
    @HasPermissionJobsSettingsUpdate
    public JobSettingsResponse getJobSettings(@PathVariable("name") String name)
    {
        return mapToResponse(jobService.getJobSettings(name),
                jobService.getLastJobExecution(name),
                jobService.getNextExecution(name).orElse(null));
    }

    @PostMapping("/{name}/run-now")
    @HasPermissionJobsSettingsUpdate
    public ResponseEntity<?> runJobNow(@PathVariable("name") String name)
    {
        boolean jobRun = jobService.runNow(name);
        if (jobRun)
        {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{name}")
    @HasPermissionJobsSettingsUpdate
    public JobSettingsResponse updateJobSettings(@PathVariable("name") String name,
                                                 @RequestBody JobSettingsUpdate request)
    {


        return mapToResponse(jobService.updateJobSettings(name,
                request.isEnabled(),
                request.getCron(),
                request.getParameters()),
                jobService.getLastJobExecution(name),
                jobService.getNextExecution(name).orElse(null)
        );
    }

    private JobSettingsResponse mapToResponse(JobSettings jobSettings,
                                              Optional<JobExecution> jobExecution,
                                              OffsetDateTime nextExecutionDate)
    {
        return new JobSettingsResponse(
                jobSettings.getName(),
                jobSettings.isEnabled(),
                jobSettings.getCron(),
                jobSettings.getParameters().values().stream().toList(),
                jobExecution.map(JobExecution::getStartDate).orElse(null),
                jobExecution.map(JobExecution::getFinishDate).orElse(null),
                jobExecution.map(JobExecution::getMessage).orElse(null),
                jobExecution.map(JobExecution::getStatus).map(JobExecutionStatus::getCode).orElse(null),
                nextExecutionDate
        );
    }
}
