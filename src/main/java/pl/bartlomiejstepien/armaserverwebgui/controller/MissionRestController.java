package pl.bartlomiejstepien.armaserverwebgui.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.controller.response.RestErrorResponse;
import pl.bartlomiejstepien.armaserverwebgui.controller.validator.MissionFileValidator;
import pl.bartlomiejstepien.armaserverwebgui.exception.MissionFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.exception.NotAllowedFileTypeException;
import pl.bartlomiejstepien.armaserverwebgui.model.Missions;
import pl.bartlomiejstepien.armaserverwebgui.service.MissionService;
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

    @GetMapping
    public Mono<GetMissionsResponse> getMissions()
    {
        return Mono.just(this.missionService.getMissions())
                .map(GetMissionsResponse::of);
    }

    @PostMapping("/enabled")
    public Mono<ResponseEntity<?>> saveEnabledMissionList(@RequestBody SaveEnabledMissionListRequest saveEnabledMissionListRequest)
    {
        return Mono.empty().doFirst(() -> this.missionService.saveEnabledMissionList(saveEnabledMissionListRequest.getMissions()))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> uploadMissionFile(@RequestPart("file") Mono<FilePart> multipartFile)
    {
        return multipartFile
                .doOnNext(missionFileValidator::validate)
                .doOnNext(filePart -> log.info("Uploading mission '{}' ", filePart.filename()))
                .flatMap(missionService::save)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @DeleteMapping(value = "/{missionName}", consumes = MediaType.ALL_VALUE)
    public Mono<ResponseEntity<?>> deleteMission(@PathVariable("missionName") String missionName)
    {
        return Mono.just(this.missionService.deleteMission(missionName))
                .thenReturn(ResponseEntity.ok().build());
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
    private static class GetMissionsResponse
    {
        List<String> disabledMissions;
        List<String> enabledMissions;

        private static GetMissionsResponse of(Missions missions)
        {
            return new GetMissionsResponse(missions.getDisabledMissions(), missions.getEnabledMissions());
        }
    }

    @Data
    private static class SaveEnabledMissionListRequest
    {
        List<String> missions;
    }
}
