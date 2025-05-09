package pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfileEntity;

import java.util.List;

public interface DifficultyService
{
    String getActiveDifficultyProfile();

    List<DifficultyProfile> getDifficultyProfiles();

    DifficultyProfileEntity saveDifficultyProfile(DifficultyProfile difficultyProfile);

    void deleteDifficultyProfile(int id);

    void deleteDifficultyProfile(String name);
}
