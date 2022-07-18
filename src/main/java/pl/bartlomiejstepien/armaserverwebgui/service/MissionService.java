package pl.bartlomiejstepien.armaserverwebgui.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface MissionService
{
    Mono<Void> save(FilePart multipartFile);
}
