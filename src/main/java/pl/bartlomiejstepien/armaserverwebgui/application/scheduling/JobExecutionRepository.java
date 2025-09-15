package pl.bartlomiejstepien.armaserverwebgui.application.scheduling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.model.JobExecutionEntity;

import java.util.Optional;

@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecutionEntity, Long>
{
    Optional<JobExecutionEntity> findFirstByJobName(String jobName);
}
