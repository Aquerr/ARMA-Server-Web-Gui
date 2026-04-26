package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionAdd;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionDelete;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionUpdate;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.MissionService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Missions;
import pl.bartlomiejstepien.armaserverwebgui.web.converter.MissionApiModelConverter;
import pl.bartlomiejstepien.armaserverwebgui.web.model.MissionApiModel;

import java.util.List;

@RestController
@RequestMapping("/api/v1/missions")
@AllArgsConstructor
@Slf4j
public class MissionRestController
{
    private final MissionService missionService;
    private final MissionApiModelConverter missionApiModelConverter;

    @HasPermissionMissionView
    @GetMapping
    public GetMissionsResponse getMissions()
    {
        Missions missions = this.missionService.getMissions();
        return GetMissionsResponse.of(missions.getEnabledMissions().stream().map(this.missionApiModelConverter::toApiModel).toList(),
                missions.getDisabledMissions().stream().map(this.missionApiModelConverter::toApiModel).toList()
        );
    }

    @HasPermissionMissionUpdate
    @PostMapping("/enabled")
    public ResponseEntity<?> saveEnabledMissionList(@RequestBody SaveEnabledMissionListRequest saveEnabledMissionListRequest)
    {
        this.missionService.saveEnabledMissionList(saveEnabledMissionListRequest.getMissionTemplates());
        return ResponseEntity.ok().build();
    }

    @HasPermissionMissionAdd
    @PostMapping("/template")
    public ResponseEntity<?> addMission(@RequestBody AddMissionRequest addMissionRequest)
    {
        String template = addMissionRequest.getTemplate();
        if (template.contains(" "))
            throw new IllegalArgumentException("Mission template should not contains whitespace!");

        missionService.addMission(addMissionRequest.getName(), template);
        return ResponseEntity.ok().build();
    }

    @HasPermissionMissionDelete
    @DeleteMapping(value = "/template")
    public ResponseEntity<?> deleteMission(@RequestBody DeleteMissionRequest deleteMissionRequest)
    {
        this.missionService.deleteMission(deleteMissionRequest.getTemplate());
        return ResponseEntity.ok().build();
    }

    @HasPermissionMissionUpdate
    @PutMapping("/id/{id}")
    public void updateMission(@PathVariable("id") long id,
                              @RequestBody MissionApiModel mission)
    {
        this.missionService.updateMission(id, this.missionApiModelConverter.toDto(mission));
    }

    /**
     * @param disabledMissions Create separate Mission dto for REST
     */
    public record GetMissionsResponse(List<MissionApiModel> disabledMissions, List<MissionApiModel> enabledMissions)
    {
        private static GetMissionsResponse of(List<MissionApiModel> enabled, List<MissionApiModel> disabled)
        {
            return new GetMissionsResponse(disabled, enabled);
        }
    }

    @Data
    public static class SaveEnabledMissionListRequest
    {
        private List<String> missionTemplates;
    }

    @Data
    public static class AddMissionRequest
    {
        private String name;
        private String template;
    }

    @Data
    public static class DeleteMissionRequest
    {
        private String template;
    }
}
