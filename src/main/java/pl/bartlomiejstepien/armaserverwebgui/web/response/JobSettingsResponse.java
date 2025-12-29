package pl.bartlomiejstepien.armaserverwebgui.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.bartlomiejstepien.armaserverwebgui.domain.job.model.JobSettings;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSettingsResponse
{
    private String name;
    private boolean enabled;
    private String cron;
    private List<JobSettings.JobParameter> parameters = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime lastExecutionDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime lastExecutionFinishedDate;
    private String lastMessage;
    private String lastStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime nextExecutionDate;

    @Builder
    public record JobParameter(String name, String description, String value)
    { }
}
