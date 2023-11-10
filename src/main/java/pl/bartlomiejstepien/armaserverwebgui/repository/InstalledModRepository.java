package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface InstalledModRepository extends ReactiveCrudRepository<InstalledModEntity, Long>
{
    Mono<InstalledModEntity> findByName(String name);

    Mono<InstalledModEntity> findByWorkshopFileId(Long id);

    Flux<InstalledModEntity> findAllByOrderByNameAsc();
}
