package pl.bartlomiejstepien.armaserverwebgui.application.scheduling.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "job_execution")
public class JobExecutionEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "job_name", nullable = false, unique = true)
    private String jobName;

    @Column(name = "last_execution_date", unique = false, nullable = false)
    private OffsetDateTime lastExecutionDate;

    @Column(name = "status", unique = false, nullable = false)
    private String status;

    @Column(name = "message", unique = false, nullable = true)
    private String message;
}
