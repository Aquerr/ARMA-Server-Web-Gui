package pl.bartlomiejstepien.armaserverwebgui.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetEntity;

@Repository
public interface ModPresetEntryRepository extends JpaRepository<ModPresetEntity.EntryEntity, Long>
{
    List<ModPresetEntity.EntryEntity> findAllByModPresetId(Long presetId);
}
