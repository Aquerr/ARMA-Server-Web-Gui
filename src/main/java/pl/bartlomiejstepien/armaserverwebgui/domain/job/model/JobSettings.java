package pl.bartlomiejstepien.armaserverwebgui.domain.job.model;

import lombok.Builder;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
@Builder
public class JobSettings
{
    String name;
    boolean enabled;
    String cron;

    @Builder.Default
    Map<String, JobParameter> parameters = new HashMap<>();

    @Builder
    public record JobParameter(String name, String description, String value)
    {
    }
}
