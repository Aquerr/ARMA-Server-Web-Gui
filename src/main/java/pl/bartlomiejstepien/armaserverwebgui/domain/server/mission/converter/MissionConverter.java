package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model.MissionEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;

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

        try
        {
            entity.setParametersJson(objectMapper.writeValueAsString(mission.getParameters()));
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
        return entity;
    }

    public Mission convertToDomainMission(MissionEntity entity)
    {
        try
        {
            return Mission.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .template(entity.getTemplate())
                    .enabled(entity.isEnabled())
                    .difficulty(Mission.Difficulty.findOrDefault(entity.getDifficulty()))
                    .parameters(objectMapper.readValue(entity.getParametersJson(), new TypeReference<>()
                    {
                    }))
                    .build();
        }
        catch (JsonProcessingException e)
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
