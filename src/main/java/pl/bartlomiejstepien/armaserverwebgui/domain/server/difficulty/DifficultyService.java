package pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfile;

import java.nio.file.Path;
import java.util.List;

public interface DifficultyService
{
    Path getProfilesDirectory();

    String getActiveDifficultyProfile();

    List<DifficultyProfile> getDifficultyProfiles();

    DifficultyProfile getDifficultyProfile(String name);

    void saveDifficultyProfile(DifficultyProfile difficultyProfile);

    void deleteDifficultyProfile(int id);

    void deleteDifficultyProfile(String name);

    void importDifficultyProfileFromFile(String profileName);
}
