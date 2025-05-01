package pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty;

import java.util.List;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfileEntity;

public interface DifficultyService
{
    String getActiveDifficultyProfile();

    List<DifficultyProfile> getDifficultyProfiles();

    DifficultyProfileEntity saveDifficultyProfile(DifficultyProfile difficultyProfile);

    void deleteDifficultyProfile(int id);

    void deleteDifficultyProfile(String name);
}
