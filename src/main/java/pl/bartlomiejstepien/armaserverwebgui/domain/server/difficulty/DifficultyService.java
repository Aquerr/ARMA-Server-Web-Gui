package pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfileEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DifficultyService
{
    Mono<String> getActiveDifficultyProfile();

    Flux<DifficultyProfile> getDifficultyProfiles();

    Mono<DifficultyProfileEntity> saveDifficultyProfile(DifficultyProfile difficultyProfile);

    Mono<Void> deleteDifficultyProfile(int id);

    Mono<Void> deleteDifficultyProfile(String name);
}
