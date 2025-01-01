package pl.bartlomiejstepien.armaserverwebgui.interfaces.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface InstalledModRepository extends ReactiveCrudRepository<InstalledModEntity, Long>
{
    Mono<InstalledModEntity> findByName(String name);

    Mono<InstalledModEntity> findByWorkshopFileId(Long id);

    Flux<InstalledModEntity> findAllByOrderByNameAsc();

    @Modifying
    @Query("UPDATE installed_mod SET installed_mod.enabled = false")
    Mono<Void> disableAllMods();

    @Modifying
    @Query("UPDATE installed_mod SET installed_mod.enabled = true WHERE installed_mod.workshop_file_id IN (:workshopFileIds)")
    Mono<Void> enableMods(@Param("workshopFileIds") List<Long> workshopFileIds);

    @Modifying
    @Query("UPDATE installed_mod SET installed_mod.server_mod = true WHERE installed_mod.workshop_file_id IN (:workshopFileIds)")
    Mono<Void> setServerMods(@Param("workshopFileIds") List<Long> workshopFileIds);
}
