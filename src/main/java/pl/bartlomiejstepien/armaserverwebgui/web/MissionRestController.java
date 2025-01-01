package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionAdd;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionDelete;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionUpdate;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionUpload;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionDoesNotExistException;
import pl.bartlomiejstepien.armaserverwebgui.web.response.RestErrorResponse;
import pl.bartlomiejstepien.armaserverwebgui.web.validator.MissionFileValidator;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.web.exception.NotAllowedFileTypeException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Missions;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.MissionService;
import reactor.core.publisher.Mono;

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
    public Mono<GetMissionsResponse> getMissions()
    {
        return this.missionService.getMissions()
                .map(GetMissionsResponse::of);
    }

    @HasPermissionMissionUpdate
    @PostMapping("/enabled")
    public Mono<ResponseEntity<?>> saveEnabledMissionList(@RequestBody SaveEnabledMissionListRequest saveEnabledMissionListRequest)
    {
        return this.missionService.saveEnabledMissionList(saveEnabledMissionListRequest.getMissions())
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @HasPermissionMissionAdd
    @PostMapping("/template")
    public Mono<ResponseEntity<?>> addMission(@RequestBody AddMissionRequest addMissionRequest)
    {
        String template = addMissionRequest.getTemplate();
        if (template.contains(" "))
            throw new IllegalArgumentException("Mission template should not contains whitespace!");

        return missionService.addMission(addMissionRequest.getName(), template)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @HasPermissionMissionUpload
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> uploadMissionFile(@RequestPart("file") Mono<FilePart> multipartFile)
    {
        return multipartFile
                .doOnNext(missionFileValidator::validate)
                .doOnNext(filePart -> log.info("Uploading mission '{}' ", filePart.filename()))
                .flatMap(missionService::save)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @HasPermissionMissionDelete
    @DeleteMapping(value = "/template/{template}")
    public Mono<ResponseEntity<?>> deleteMission(@PathVariable("template") String template)
    {
        return this.missionService.deleteMission(template)
                .thenReturn(ResponseEntity.ok().build());
    }

    @HasPermissionMissionUpdate
    @PutMapping("/id/{id}")
    public Mono<Void> updateMission(@PathVariable("id") long id,
                                    @RequestBody Mission mission)
    {
        return this.missionService.updateMission(id, mission);
    }

    @ExceptionHandler(value = MissionDoesNotExistException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public RestErrorResponse onMissionDoesNotExist(MissionDoesNotExistException exception)
    {
        return RestErrorResponse.of("Mission does not exist for given id!", HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(value = MissionFileAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RestErrorResponse onMissionFileAlreadyExistsException(MissionFileAlreadyExistsException exception)
    {
        return RestErrorResponse.of("Mission file already exists!", HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(value = NotAllowedFileTypeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RestErrorResponse onNotAllowedFileTypeException(NotAllowedFileTypeException exception)
    {
        return RestErrorResponse.of(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
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
}
