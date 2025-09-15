package pl.bartlomiejstepien.armaserverwebgui.application.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.model.JobExecutionEntity;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecutionInfoService
{
    private final JobExecutionRepository jobExecutionRepository;

    @Transactional(readOnly = true)
    public Optional<OffsetDateTime> getLastExecutionDate(String jobName)
    {
        return jobExecutionRepository.findFirstByJobName(jobName)
                .map(JobExecutionEntity::getLastExecutionDate);
    }

    @Transactional
    public void saveJobLastExecutionDate(String name, OffsetDateTime lastExecutionDate)
    {
        log.info("Saving last execution date for job '{}' at: {}", name, lastExecutionDate);
        JobExecutionEntity jobExecutionEntity = jobExecutionRepository.findFirstByJobName(name)
                .orElse(null);

        if (jobExecutionEntity == null)
        {
            jobExecutionEntity = new JobExecutionEntity();
            jobExecutionEntity.setJobName(name);
        }
        jobExecutionEntity.setLastExecutionDate(lastExecutionDate);
        jobExecutionRepository.save(jobExecutionEntity);
    }
}
