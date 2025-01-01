package pl.bartlomiejstepien.armaserverwebgui.interfaces.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetEntity;
import reactor.core.publisher.Flux;

@Repository
public interface ModPresetEntryRepository extends ReactiveCrudRepository<ModPresetEntity.EntryEntity, Long>
{
    Flux<ModPresetEntity.EntryEntity> findAllByModPresetId(Long presetId);
}
