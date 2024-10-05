package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModSettingsEntity;
import reactor.core.publisher.Mono;

public interface ModSettingsRepository extends ReactiveCrudRepository<ModSettingsEntity, Long>
{
    Mono<ModSettingsEntity> findByName(String name);

    @Modifying
    @Query("UPDATE mod_settings SET mod_settings.active = FALSE")
    Mono<Void> disableAll();
}
