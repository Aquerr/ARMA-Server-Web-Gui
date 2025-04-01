package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionAdd;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionDelete;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionUpdate;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionUpload;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.web.validator.MissionFileValidator;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Missions;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.MissionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/missions")
@AllArgsConstructor
@Slf4j
public class MissionRestController
{
    private final MissionService missionService;
    private final MissionFileValidator missionFileValidator;

    @HasPermissionMissionView
    @GetMapping
    public GetMissionsResponse getMissions()
    {
        return GetMissionsResponse.of(this.missionService.getMissions());
    }

    @HasPermissionMissionUpdate
    @PostMapping("/enabled")
    public ResponseEntity<?> saveEnabledMissionList(@RequestBody SaveEnabledMissionListRequest saveEnabledMissionListRequest)
    {
        this.missionService.saveEnabledMissionList(saveEnabledMissionListRequest.getMissions());
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

    @HasPermissionMissionUpload
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMissionFile(@RequestPart("file") MultipartFile multipartFile)
    {
        log.info("Uploading mission '{}' ", multipartFile.getOriginalFilename());
        missionFileValidator.validate(multipartFile);
        missionService.save(multipartFile);
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
                                    @RequestBody Mission mission)
    {
        this.missionService.updateMission(id, mission);
    }

    @Value(staticConstructor = "of")
    public static class GetMissionsResponse
    {
        List<Mission> disabledMissions;
        List<Mission> enabledMissions;

        private static GetMissionsResponse of(Missions missions)
        {
            return new GetMissionsResponse(missions.getDisabledMissions(), missions.getEnabledMissions());
        }
    }

    @Data
    public static class SaveEnabledMissionListRequest
    {
        private List<Mission> missions;
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
