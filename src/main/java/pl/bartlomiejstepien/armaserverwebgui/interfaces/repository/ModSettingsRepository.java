package pl.bartlomiejstepien.armaserverwebgui.interfaces.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModSettingsEntity;
import reactor.core.publisher.Mono;

public interface ModSettingsRepository extends ReactiveCrudRepository<ModSettingsEntity, Long>
{
    Mono<ModSettingsEntity> findByName(String name);

    @Modifying
    @Query("UPDATE mod_settings SET mod_settings.active = FALSE")
    Mono<Void> disableAll();

    Mono<ModSettingsEntity> findFirstByActiveTrue();

    @Modifying
    @Query("UPDATE mod_settings SET mod_settings.active = FALSE WHERE id = :id")
    Mono<Void> deactivate(@Param("id") Long id);
}
