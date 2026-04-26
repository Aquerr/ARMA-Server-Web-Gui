package pl.bartlomiejstepien.armaserverwebgui.web.converter;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.web.model.MissionApiModel;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MissionApiModelConverter
{
    public Mission toDto(MissionApiModel mission)
    {
        if (mission == null)
            return null;

        return Mission.builder()
                .id(mission.getId())
                .name(mission.getName())
                .template(mission.getTemplate())
                .difficulty(Mission.Difficulty.findOrDefault(mission.getDifficulty()))
                .enabled(mission.isEnabled())
                .sizeBytes(Optional.ofNullable(mission.getSizeBytes()).orElse(0L))
                .parameters(Optional.ofNullable(mission.getParameters()).map(params -> params.stream()
                                .map(parameter -> new Mission.Parameter(parameter.getName(), parameter.getValue()))
                                .collect(Collectors.toSet()))
                        .orElse(Collections.emptySet()))
                .build();
    }

    public MissionApiModel toApiModel(Mission mission)
    {
        if (mission == null)
            return null;

        MissionApiModel missionApiModel = new MissionApiModel();
        missionApiModel.setId(mission.getId());
        missionApiModel.setName(mission.getName());
        missionApiModel.setTemplate(mission.getTemplate());
        missionApiModel.setEnabled(mission.isEnabled());
        missionApiModel.setDifficulty(mission.getDifficulty().name());
        missionApiModel.setSizeBytes(mission.getSizeBytes());
        missionApiModel.setParameters(mission.getParameters().stream()
                .map(parameter -> new MissionApiModel.Parameter(parameter.getName(), parameter.getValue()))
                .collect(Collectors.toSet()));
        return missionApiModel;
    }
}
