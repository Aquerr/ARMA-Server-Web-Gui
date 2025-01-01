package pl.bartlomiejstepien.armaserverwebgui.interfaces.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model.MissionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface MissionRepository extends ReactiveCrudRepository<MissionEntity, Long>
{
    @Modifying
    Mono<Void> deleteByTemplate(String template);

    @Modifying
    @Query(value = "UPDATE mission SET mission.enabled = true WHERE mission.template IN (:templates)")
    Flux<Void> updateAllByTemplateSetEnabled(@Param("templates") List<String> templates);

    @Modifying
    @Query(value = "UPDATE mission SET mission.enabled = false")
    Flux<Void> disableAll();
}
