package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfileEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.Mission;
import reactor.core.publisher.Mono;

@Repository
public interface DifficultyProfileRepository extends ReactiveCrudRepository<DifficultyProfileEntity, Integer>
{
    Mono<DifficultyProfileEntity> findByActiveTrue();

    Mono<DifficultyProfileEntity> findByName(String name);
}
