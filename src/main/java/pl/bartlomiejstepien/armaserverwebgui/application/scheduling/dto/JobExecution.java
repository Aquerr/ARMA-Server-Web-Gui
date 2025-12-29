package pl.bartlomiejstepien.armaserverwebgui.application.scheduling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.model.JobExecutionStatus;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecution
{
    private Long id;
    private String jobName;
    private OffsetDateTime startDate;
    private OffsetDateTime finishDate;
    private String message;
    private JobExecutionStatus status;
}
