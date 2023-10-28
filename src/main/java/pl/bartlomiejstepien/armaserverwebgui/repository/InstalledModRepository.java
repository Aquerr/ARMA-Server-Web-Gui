package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledMod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface InstalledModRepository extends ReactiveCrudRepository<InstalledMod, Long>
{
    Mono<InstalledMod> findByName(String name);

    Flux<InstalledMod> findAllByOrderByNameAsc();
}
