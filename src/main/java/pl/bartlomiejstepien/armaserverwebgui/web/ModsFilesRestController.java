package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModsUpload;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionModsView;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.web.validator.ModFileValidator;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/mods-files")
@AllArgsConstructor
@Slf4j
public class ModsFilesRestController
{
    private final ModService modService;
    private final ModFileValidator modFileValidator;

    @HasPermissionModsUpload
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<?> uploadModFile(@RequestPart("file") List<MultipartFile> multipartFiles,
                                           @RequestPart(value = "overwrite", required = false) String overwrite)
    {
        boolean overwriteBoolean = Optional.ofNullable(overwrite)
                .map(Boolean::parseBoolean)
                .orElse(false);

        for (MultipartFile multipartFile : multipartFiles)
        {
            modFileValidator.validate(multipartFile);
            log.info("Uploading mod '{}' ", multipartFile.getOriginalFilename());
            modService.saveModFile(multipartFile, overwriteBoolean);
        }

        return ResponseEntity.ok().build();
    }

    @HasPermissionModsView
    @GetMapping("/{name}/exists")
    public DoesModExists checkModFileExists(@PathVariable("name") String modName)
    {
        return new DoesModExists(modService.checkModFileExists(modName));
    }

    public record DoesModExists(boolean exists)
    {
    }
}
