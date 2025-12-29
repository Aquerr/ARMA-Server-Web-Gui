package pl.bartlomiejstepien.armaserverwebgui.application.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.dto.JobExecution;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.model.JobExecutionEntity;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.model.JobExecutionStatus;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecutionInfoService
{
    private final JobExecutionRepository jobExecutionRepository;

    @Transactional
    public void saveJobExecutionStart(String name)
    {
        log.info("Saving job execution start for job '{}' at: {}", name, OffsetDateTime.now());
        JobExecutionEntity jobExecutionEntity = jobExecutionRepository.findFirstByJobName(name).orElse(null);
        if (jobExecutionEntity == null)
        {
            jobExecutionEntity = new JobExecutionEntity();
        }
        jobExecutionEntity.setJobName(name);
        jobExecutionEntity.setStatus(JobExecutionStatus.STARTED.getCode());
        jobExecutionEntity.setStartDate(OffsetDateTime.now());
        jobExecutionEntity.setFinishDate(null);
        jobExecutionEntity.setMessage(null);
        jobExecutionRepository.save(jobExecutionEntity);
    }

    @Transactional
    public void saveJobExecutionFinish(String name,
                                       OffsetDateTime finishDate,
                                       JobExecutionStatus status,
                                       String message)
    {
        log.info("Saving execution finished date for job '{}' at: {}, status: {}, message: {}", name, finishDate, status, message);
        JobExecutionEntity jobExecutionEntity = jobExecutionRepository.findFirstByJobName(name)
                .orElse(null);

        if (jobExecutionEntity == null)
        {
            jobExecutionEntity = new JobExecutionEntity();
            jobExecutionEntity.setJobName(name);
        }
        jobExecutionEntity.setFinishDate(finishDate);
        jobExecutionEntity.setStatus(status.getCode());
        jobExecutionEntity.setMessage(message);
        jobExecutionRepository.save(jobExecutionEntity);
    }

    @Transactional(readOnly = true)
    public Optional<JobExecution> getLastJobExecution(String name)
    {
        return jobExecutionRepository.findFirstByJobName(name)
                .map(this::toJobExecution);
    }

    private JobExecution toJobExecution(JobExecutionEntity jobExecutionEntity)
    {
        return JobExecution.builder()
                .id(jobExecutionEntity.getId())
                .jobName(jobExecutionEntity.getJobName())
                .startDate(jobExecutionEntity.getStartDate())
                .finishDate(jobExecutionEntity.getFinishDate())
                .message(jobExecutionEntity.getMessage())
                .status(JobExecutionStatus.findByCode(jobExecutionEntity.getStatus()).orElse(null))
                .build();
    }
}
