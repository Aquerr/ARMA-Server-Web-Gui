package pl.bartlomiejstepien.armaserverwebgui.domain.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiejstepien.armaserverwebgui.application.scheduling.AswgJob;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
public class JobConfig
{
    @Bean
    public Map<String, AswgJob> aswgJobsMap(List<AswgJob> aswgJobs)
    {
        return aswgJobs.stream()
                .collect(Collectors.toMap(AswgJob::getName, aswgJob -> aswgJob));
    }
}
