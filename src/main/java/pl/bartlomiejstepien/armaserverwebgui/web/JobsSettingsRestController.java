package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionSecuritySettingsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.JobService;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.model.JobSettings;
import pl.bartlomiejstepien.armaserverwebgui.web.request.JobSettingsUpdate;
import pl.bartlomiejstepien.armaserverwebgui.web.response.JobSettingsResponse;

import java.time.OffsetDateTime;
import java.util.List;

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
        // For now, ignore names only param.
        return jobService.getJobsNames();
    }

    @GetMapping("/{name}")
    @HasPermissionSecuritySettingsView
    public JobSettingsResponse getJobSettings(@PathVariable("name") String name)
    {
        return mapToResponse(jobService.getJobSettings(name),
                jobService.getLastExecutionDate(name).orElse(null),
                jobService.getNextExecution(name).orElse(null));
    }

    @PutMapping("/{name}")
    @HasPermissionSecuritySettingsView
    public JobSettingsResponse updateJobSettings(@PathVariable("name") String name,
                                                 @RequestBody JobSettingsUpdate request)
    {
        return mapToResponse(jobService.updateJobSettings(name,
                request.isEnabled(),
                request.getCron(),
                request.getParameters()),
                jobService.getLastExecutionDate(name).orElse(null),
                jobService.getNextExecution(name).orElse(null)
        );
    }

    private JobSettingsResponse mapToResponse(JobSettings jobSettings,
                                              OffsetDateTime lastExecutionDate,
                                              OffsetDateTime nextExecutionDate)
    {
        return new JobSettingsResponse(
                jobSettings.getName(),
                jobSettings.isEnabled(),
                jobSettings.getCron(),
                jobSettings.getParameters().values().stream().toList(),
                lastExecutionDate,
                nextExecutionDate
        );
    }
}
