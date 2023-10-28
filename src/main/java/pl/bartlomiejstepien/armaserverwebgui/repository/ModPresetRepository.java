package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetEntity;
import reactor.core.publisher.Mono;

@Repository
public interface ModPresetRepository extends ReactiveCrudRepository<ModPresetEntity, Long>
{
    Mono<ModPresetEntity> findByName(String name);
}
