package pl.bartlomiejstepien.armaserverwebgui.application.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.VanillaMissionsImporter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.converter.MissionConverter;
import pl.bartlomiejstepien.armaserverwebgui.repository.MissionRepository;

@Configuration(proxyBeanMethods = false)
public class VanillaMissionsImporterConfig
{
    @Bean
    @ConditionalOnProperty(value = "aswg.vanilla-missions-importer.enabled", havingValue = "true")
    public VanillaMissionsImporter vanillaMissionsImporter(
            MissionConverter missionConverter,
            MissionRepository missionRepository)
    {
        return new VanillaMissionsImporter(missionConverter, missionRepository);
    }
}
