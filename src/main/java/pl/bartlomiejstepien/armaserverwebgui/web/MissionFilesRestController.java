package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionDownload;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionUpload;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionMissionView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.MissionService;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionNotFoundException;
import pl.bartlomiejstepien.armaserverwebgui.web.validator.MissionFileValidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/missions-files")
@Slf4j
public class MissionFilesRestController
{
    private final MissionService missionService;
    private final MissionFileValidator missionFileValidator;

    @HasPermissionMissionUpload
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<?> uploadMissionFile(@RequestPart("file") List<MultipartFile> multipartFiles,
                                           @RequestPart(value = "overwrite", required = false) String overwrite)
    {
        boolean overwriteBoolean = Optional.ofNullable(overwrite)
                .map(Boolean::parseBoolean)
                .orElse(false);

        for (MultipartFile multipartFile : multipartFiles)
        {
            missionFileValidator.validate(multipartFile);
            log.info("Uploading mission '{}' ", multipartFile.getOriginalFilename());
            missionService.save(multipartFile, overwriteBoolean);
        }

        return ResponseEntity.ok().build();
    }

    @HasPermissionMissionView
    @GetMapping("/{name}/exists")
    public MissionExistenceCheckResponse checkMissionFileExists(@PathVariable("name") String missionName)
    {
        return new MissionExistenceCheckResponse(missionService.checkMissionFileExists(missionName));
    }

    @HasPermissionMissionDownload
    @GetMapping(value = "/{missionId}/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<InputStreamResource> downloadMissionFile(@PathVariable("missionId") long missionId)
    {
        File file = this.missionService.getMissionFile(missionId);
        if (file == null)
            throw new MissionNotFoundException(missionId);

        try
        {
            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(inputStreamResource);
        }
        catch (FileNotFoundException e)
        {
            throw new MissionNotFoundException(missionId);
        }
    }

    public record MissionExistenceCheckResponse(boolean exists)
    {
    }
}
