package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model.MissionEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MissionConverter
{
    private final ObjectMapper objectMapper;

    public MissionEntity convertToEntity(Mission mission)
    {
        MissionEntity entity = new MissionEntity();
        entity.setId(mission.getId());
        entity.setName(mission.getName());
        entity.setTemplate(mission.getTemplate());
        entity.setEnabled(mission.isEnabled());
        entity.setDifficulty(mission.getDifficulty().name());
        if (!mission.getParameters().isEmpty())
        {
            try
            {
                // Skip nullable keys
                Set<Mission.Parameter> correctedParameters = mission.getParameters().stream()
                        .filter(parameter -> StringUtils.hasText(parameter.getName()))
                        .collect(Collectors.toSet());
                entity.setParametersJson(objectMapper.writeValueAsString(correctedParameters));
            }
            catch (JacksonException e)
            {
                throw new RuntimeException(e);
            }
        }
        return entity;
    }

    public Mission convertToDomainMission(MissionEntity entity)
    {
        try
        {
            Set<Mission.Parameter> parameters = new HashSet<>();
            if (entity.getParametersJson() != null)
            {
                parameters = objectMapper.readValue(entity.getParametersJson(), new TypeReference<>()
                {
                });
            }

            return Mission.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .template(entity.getTemplate())
                    .enabled(entity.isEnabled())
                    .difficulty(Mission.Difficulty.findOrDefault(entity.getDifficulty()))
                    .parameters(parameters)
                    .build();
        }
        catch (JacksonException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Mission convertToDomainMission(ArmaServerConfig.Missions.Mission armaMission)
    {
        Mission mission = new Mission();
        mission.setName(armaMission.getTemplate());
        mission.setDifficulty(Mission.Difficulty.findOrDefault(Optional.ofNullable(armaMission.getDifficulty())
                .map(String::toUpperCase)
                .orElse(null)));
        mission.setParameters(convertToDomainMissionParameters(armaMission.getParams()));
        return mission;
    }

    public ArmaServerConfig.Missions.Mission convertToArmaMissionObject(Mission mission)
    {
        ArmaServerConfig.Missions.Mission armaMission = new ArmaServerConfig.Missions.Mission();
        armaMission.setTemplate(mission.getTemplate());
        armaMission.setDifficulty(mission.getDifficulty().name().toLowerCase());
        armaMission.setParams(convertToArmaMissionParams(mission.getParameters()));
        return armaMission;
    }

    private Set<Mission.Parameter> convertToDomainMissionParameters(Map<String, String> params)
    {
        return params.entrySet().stream()
                .map(entry -> new Mission.Parameter(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    private Map<String, String> convertToArmaMissionParams(Set<Mission.Parameter> parameters)
    {
        return parameters.stream()
                .collect(Collectors.toMap(Mission.Parameter::getName, Mission.Parameter::getValue));
    }
}
