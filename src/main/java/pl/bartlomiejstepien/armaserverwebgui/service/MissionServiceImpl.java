package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.exception.MissionFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.storage.MissionStorage;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class MissionServiceImpl implements MissionService
{
    private final MissionStorage missionStorage;

    @Override
    public Mono<Void> save(FilePart multipartFile)
    {
        if (missionStorage.doesMissionExists(multipartFile.filename()))
            throw new MissionFileAlreadyExistsException();

        try
        {
            return missionStorage.save(multipartFile);
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public List<String> getInstalledMissionNames()
    {
        return this.missionStorage.getInstalledMissionNames();
    }

    @Override
    public boolean deleteMission(String missionName)
    {
        return this.missionStorage.deleteMission(missionName);
    }
}
