package pl.bartlomiejstepien.armaserverwebgui.interfaces.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfileEntity;
import reactor.core.publisher.Mono;

@Repository
public interface DifficultyProfileRepository extends ReactiveCrudRepository<DifficultyProfileEntity, Integer>
{
    Mono<DifficultyProfileEntity> findFirstByActiveTrue();

    Mono<DifficultyProfileEntity> findByName(String name);
}
