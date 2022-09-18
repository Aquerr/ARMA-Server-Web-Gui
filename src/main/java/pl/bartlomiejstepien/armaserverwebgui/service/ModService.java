package pl.bartlomiejstepien.armaserverwebgui.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ModService
{
    Mono<Void> save(FilePart multipartFile);

    List<String> getInstalledModNames();

    boolean deleteMod(String modName);
}
