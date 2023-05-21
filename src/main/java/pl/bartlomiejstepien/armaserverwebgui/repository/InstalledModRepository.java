package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.InstalledMod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InstalledModRepository extends ReactiveCrudRepository<InstalledMod, Long>
{
    Mono<InstalledMod> findByName(String name);

    Flux<InstalledMod> findAllByOrderByNameAsc();
}
