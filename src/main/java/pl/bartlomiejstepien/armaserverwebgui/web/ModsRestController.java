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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.web.response.RestErrorResponse;
import pl.bartlomiejstepien.armaserverwebgui.web.validator.ModFileValidator;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception.ModFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.web.exception.NotAllowedFileTypeException;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Mods;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/mods")
@AllArgsConstructor
@Slf4j
public class ModsRestController
{
    private final ModService modService;
    private final ModFileValidator modFileValidator;

    @GetMapping
    public Mono<GetModsResponse> getMods()
    {
        return Mono.just(this.modService.getMods()).map(GetModsResponse::of);
    }

    @PostMapping("/enabled")
    public Mono<ResponseEntity<?>> saveEnabledModsList(@RequestBody SaveEnabledModsListRequest saveEnabledModsListRequest)
    {
        return Mono.empty().doFirst(() -> this.modService.saveEnabledModList(saveEnabledModsListRequest.getMods()))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> uploadModFile(@RequestPart("file") Mono<FilePart> multipartFile)
    {
        return multipartFile
                .doOnNext(modFileValidator::validate)
                .doOnNext(filePart -> log.info("Uploading mod '{}' ", filePart.filename()))
                .flatMap(modService::save)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @DeleteMapping(value = "/{modName}", consumes = MediaType.ALL_VALUE)
    public Mono<ResponseEntity<?>> deleteMod(@PathVariable("modName") String modName)
    {
        return Mono.just(this.modService.deleteMod(modName))
                .thenReturn(ResponseEntity.ok().build());
    }

    @ExceptionHandler(value = ModFileAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RestErrorResponse onModsFileAlreadyExistsException(ModFileAlreadyExistsException exception)
    {
        return RestErrorResponse.of("Mod file already exists!", HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(value = NotAllowedFileTypeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public RestErrorResponse onNotAllowedFileTypeException(NotAllowedFileTypeException exception)
    {
            return RestErrorResponse.of(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @Value(staticConstructor = "of")
    private static class GetModsResponse
    {
        Set<String> disabledMods;
        Set<String> enabledMods;

        private static GetModsResponse of(Mods mods)
        {
            return new GetModsResponse(mods.getDisabledMods(), mods.getEnabledMods());
        }
    }

    @Data
    private static class SaveEnabledModsListRequest
    {
        Set<String> mods;
    }
}
