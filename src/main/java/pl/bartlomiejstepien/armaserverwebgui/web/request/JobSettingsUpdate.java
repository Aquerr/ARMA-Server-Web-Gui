package pl.bartlomiejstepien.armaserverwebgui.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSettingsUpdate
{
    private boolean enabled;
    private String cron;
    private Map<String, String> parameters;
}
