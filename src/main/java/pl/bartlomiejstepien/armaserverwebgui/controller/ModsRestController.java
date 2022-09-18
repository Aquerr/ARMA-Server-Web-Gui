package pl.bartlomiejstepien.armaserverwebgui.controller;


import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.armaserverwebgui.controller.validator.ModFileValidator;
import pl.bartlomiejstepien.armaserverwebgui.exception.ModFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.exception.NotAllowedFileTypeException;
import pl.bartlomiejstepien.armaserverwebgui.service.ModService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mods")
@AllArgsConstructor
@Slf4j
public class ModsRestController
{
    private final ModService modService;
    private final ModFileValidator modFileValidator;

    @GetMapping
    public Mono<GetInstalledModsResponse> getInstalledMods()
    {
        return Mono.just(this.modService.getInstalledModNames()).map(GetInstalledModsResponse::of);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> uploadModFile(@RequestPart("file") Mono<FilePart> multipartFile)
    {
        return multipartFile
                .map(filePart -> {
                    if(modFileValidator.isValid(filePart))
                        return filePart;
                    throw new NotAllowedFileTypeException("Wrong file type! Only .zip files are supported!");
                })
                .doOnNext(filePart -> log.info("Uploading mod '{}' ", filePart.filename()))
                .flatMap(modService::save)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @DeleteMapping(value = "/{modName}", consumes = MediaType.ALL_VALUE)
    public Mono<ResponseEntity<?>> deleteMission(@PathVariable("modName") String modName)
    {
        return Mono.just(this.modService.deleteMod(modName))
                .thenReturn(ResponseEntity.ok().build());
    }

    @ExceptionHandler(value = ModFileAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse onModsFileAlreadyExistsException(ModFileAlreadyExistsException exception)
    {
        return ErrorResponse.of("Mod file already exists!", HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(value = NotAllowedFileTypeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse onNotAllowedFileTypeException(NotAllowedFileTypeException exception)
    {
        return ErrorResponse.of("Wrong file type! Only .zip files are supported!", HttpStatus.BAD_REQUEST.value());
    }

    @Value(staticConstructor = "of")
    private static class GetInstalledModsResponse
    {
        List<String> mods;
    }

    @Value(staticConstructor = "of")
    private static class ErrorResponse
    {
        String message;
        int code;
    }
}
