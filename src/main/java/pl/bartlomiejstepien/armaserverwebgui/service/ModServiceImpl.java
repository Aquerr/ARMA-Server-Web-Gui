package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.exception.MissionFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.storage.ModStorage;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ModServiceImpl implements ModService
{
    private final ModStorage modStorage;

    @Override
    public Mono<Void> save(FilePart multipartFile)
    {
        if(modStorage.doesModExists(multipartFile.filename()))
            throw new MissionFileAlreadyExistsException();

        try
        {
            return modStorage.save(multipartFile);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getInstalledModNames()
    {
        return this.modStorage.getInstalledModNames();
    }
}
